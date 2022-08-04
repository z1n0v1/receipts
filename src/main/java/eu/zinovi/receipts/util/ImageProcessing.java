package eu.zinovi.receipts.util;

import boofcv.abst.distort.FDistort;
import boofcv.abst.feature.detect.line.DetectLineSegment;
import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.feature.detect.line.ConfigLineRansac;
import boofcv.factory.feature.detect.line.FactoryDetectLine;
import boofcv.factory.fiducial.ConfigQrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_F32;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageProcessing {
    public static ByteArrayInputStream graphicallyProcessReceipt(BufferedImage image) {
        GrayF32 gray = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);

        DetectLineSegment<GrayF32> detector = FactoryDetectLine.
                lineRansac(new ConfigLineRansac(
                                40, 30, 2.36, true),
                        GrayF32.class);
        List<LineSegment2D_F32> found = detector.detect(gray);

        Point2D_F32 bottomRight = new Point2D_F32(0, 0);
        Point2D_F32 topLeft = new Point2D_F32(image.getWidth(), image.getHeight());
        for (LineSegment2D_F32 line : found) {
            if (line.a.x < topLeft.x) {
                topLeft.x = line.a.x;
            }
            if (line.a.y < topLeft.y) {
                topLeft.y = line.a.y;
            }
            if (line.b.x > bottomRight.x) {
                bottomRight.x = line.b.x;
            }
            if (line.b.y > bottomRight.y) {
                bottomRight.y = line.b.y;
            }
        }

        if (topLeft.x > 10) {
            topLeft.x -= 10;
        }
        if (bottomRight.x < image.getWidth() - 10) {
            bottomRight.x += 10;
        }

        GrayF32 sub = gray.subimage((int) topLeft.x, (int) topLeft.y,
                (int) bottomRight.x, (int) bottomRight.y);

        GrayF32 scaled = new GrayF32(640, sub.height * 640 / sub.width);
        new FDistort(sub, scaled).scaleExt().apply();

        BufferedImage output = ConvertBufferedImage.convertTo(sub, null, true);

        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(0.7f);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream outputStream = new MemoryCacheImageOutputStream(os);
        jpgWriter.setOutput(outputStream);
        IIOImage outputImage = new IIOImage(output, null, null);
        try {
            jpgWriter.write(null, outputImage, jpgWriteParam);
        } catch (IOException e) {
            throw new ReceiptProcessException("Грешка при обработка на изображението.", e);
        }
        jpgWriter.dispose();

        return new ByteArrayInputStream(os.toByteArray());
    }

    public static String readQRCode(BufferedImage receiptImage) {
        GrayU8 gray = ConvertBufferedImage.convertFrom(receiptImage, (GrayU8) null);
        var config = new ConfigQrCode();
        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(config, GrayU8.class);
        detector.process(gray);
        List<QrCode> detections = detector.getDetections();

        if (detections.isEmpty()) {
            return null;
        }

        return detections.get(0).message;
    }

    public static ByteArrayInputStream compressAndScaleProfilePicture(InputStream picture) throws IOException {
        BufferedImage image = ImageIO.read(picture);

        // scale image to 96x96
        AffineTransform transform = AffineTransform.getScaleInstance(
                96.0 / image.getWidth(), 96.0 / image.getHeight());
        BufferedImage processedImage = new BufferedImage(96, 96,
                BufferedImage.TYPE_INT_ARGB);
        AffineTransformOp scaleOp =
                new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        processedImage = scaleOp.filter(image, processedImage);

        // Remove alpha channel
        BufferedImage target = new BufferedImage(96, 96, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = target.createGraphics();

        g.fillRect(0, 0, processedImage.getWidth(), processedImage.getHeight());
        g.drawImage(processedImage, 0, 0, null);
        g.dispose();

        // compress image to jpeg
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(0.7f);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream outputStream = new MemoryCacheImageOutputStream(os);
        jpgWriter.setOutput(outputStream);
        IIOImage outputImage = new IIOImage(target, null, null);
        try {
            jpgWriter.write(null, outputImage, jpgWriteParam);
        } catch (IOException e) {
            throw new IllegalArgumentException("Грешка при обработка на изображението.", e);
        }
        jpgWriter.dispose();

        return new ByteArrayInputStream(os.toByteArray());

    }
}
