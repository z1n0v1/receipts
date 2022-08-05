
function drawPolygons(receiptId, bucket, canvasId, imageId) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext("2d");
    var img = document.getElementById(imageId);
    canvas.height = img.height
    canvas.width = img.width

    ctx.drawImage(img, 0, 0);

    console.log("drawing polygons");

    const myRequest = new Request("https://" + bucket + ".storage.googleapis.com/json/poly/" + receiptId + ".json");
    console.log("myRequest: " + myRequest);
    fetch(myRequest)
        .then(response => response.json())
        .then(data => {
            var canvas = document.getElementById(canvasId);
            var context = canvas.getContext('2d');
            var img = document.getElementById(imageId);

            console.log("fetched polygons");

            context.drawImage(img, 0, 0);

            for (const polygon of data) {
                context.beginPath();
                context.moveTo(polygon.x1, polygon.y1);
                context.lineTo(polygon.x2, polygon.y2);
                context.lineTo(polygon.x3, polygon.y3);
                context.lineTo(polygon.x4, polygon.y4);
                context.lineTo(polygon.x1, polygon.y1);
                context.font = "8px Arial";
                context.fillText(polygon.label, polygon.x1, polygon.y1);

                context.strokeStyle = 'blue';
                context.lineWidth = 3;
                context.stroke();
                context.closePath();
            }
        });
}

function drawMLPolygons(receiptId, bucket,  canvasId, imageId) {
    console.log("drawMLPolygons");
    if(document.readyState === "complete") {
        drawPolygons(receiptId, bucket,  canvasId, imageId);
    } else {
        window.addEventListener("load", event => {
            var image = document.getElementById(imageId);
            if (image.complete && image.naturalHeight !== 0) {
                drawPolygons(receiptId, bucket, canvasId, imageId);
            }
        });
    }
    /*]]>*/
}