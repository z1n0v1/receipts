$(document).ready(function () {
    $('#document-title').text(document.title);
});

function uploadReceipt(e) {

    let upBtn = document.getElementById('receipt-upload');
    upBtn.disabled = true;

    let spinner = $('#spinner-upload');
    spinner.removeClass('d-none');

    let formData = new FormData();
    formData.append('file', e.files[0]);

    $.ajax({
        url: '/api/receipts/add',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            location.href = '/receipts/details/' + data[0];
        },
        error: function (data) {
            try {
                newPageAlert(JSON.parse(xhr.responseText).message, "danger", true);
            } catch (e) {
                location.reload();
            }
            spinner.addClass('d-none');
            upBtn.disabled = false;
        }
    });
}

function liveUpdatesInit() {
    var sock = new SockJS('/alert-messages');
    var stompClient = Stomp.over(sock);
    stompClient.debug = null
    var uploadErrors = false;

    stompClient.connect({}, function () {
        var sessionId = /\/([^\/]+)\/websocket/.exec(sock._transport.url)[1];

        stompClient.subscribe('/topic/alert-messages-user' + sessionId, function (message) {
            var currentMessage = JSON.parse(message.body);
            sleep(500).then(() => {
                newPageAlert(currentMessage.message, currentMessage.type);
                if (currentMessage.type === 'danger') {
                    uploadErrors = true;
                }
            });
        });
    });
}

function pageAlert(...args) {
    let pageAlert = $('#page-alert');
    let alertClass = args[0];
    let message = args[1];
    if (args.length >= 3) {
        pageAlert = $(args[2]);
    }
    let fade = true;
    if (args.length >= 4) {
        fade = args[3];
    }

    pageAlert.removeClass();
    pageAlert.addClass(alertClass + ' alert-');
    pageAlert.text(message);
    pageAlert.show();
    if (fade) {
        pageAlert.fadeOut(5000);
    }
}

function newPageAlert(...args) {

    let pageAlert = $('#page-alert');
    let alertClass = "info";
    let message = args[0];
    let fade = false;

    if (args[1]) {
        alertClass = args[1];
    }
    if (args[2]) {
        fade = args[2];
    }
    if (args[3]) {
        pageAlert = $(args[3]);
    }

    pageAlert.removeClass();
    pageAlert.addClass('h-25 d-inline-block alert-' + alertClass );
    pageAlert.text(message);
    pageAlert.show();
    // pageAlert.css("display", "inline-block");
    if (fade) {
        pageAlert.fadeOut(5000);
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

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
        // window.addEventListener("load", event => {
        var image = document.getElementById(imageId);
        // var image = document.querySelector('#' + imageId);
        if (image.complete && image.naturalHeight !== 0) {
            drawPolygons(receiptId, bucket, canvasId, imageId);
        }
    });
    }
    /*]]>*/
}