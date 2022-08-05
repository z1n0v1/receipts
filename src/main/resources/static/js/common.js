
var sockJS = new SockJS('/alert-messages');

$(document).ready(function () {
    $('#document-title').text(document.title);
    if (isAuthenticated) {
        liveUpdatesInit();
        $(window).on('beforeunload', function () {
            sockJS.close();
        });
    }
});

function uploadReceipt(e) {

    let upBtn = document.getElementById('receipt-upload');
    upBtn.disabled = true;

    let spinner = $('#spinner-upload');
    spinner.removeClass('d-none');

    let formData = new FormData();
    formData.append('file', e.files[0]);

    $.ajax({
        url: '/api/receipt',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            if (data.length > 0) {
                location.href = '/receipt/details/' + data[0];
            } else {
                upBtn.disabled = false;
                spinner.addClass('d-none');
                sleep(3000).then(() => {
                    pageAlert('Възникна проблем с разчитането на касовата бележка', 'danger', true);
                });
            }
        },
        error: function (data) {
            try {
                pageAlert(JSON.parse(data.responseText).message, "danger", true);
            } catch (e) {
                location.reload();
            }
            spinner.addClass('d-none');
            upBtn.disabled = false;
        }
    });
}

function liveUpdatesInit() {
    var stompClient = Stomp.over(sockJS);
    stompClient.debug = null
    var uploadErrors = false;

    stompClient.connect({}, function () {
        var sessionId = /\/([^\/]+)\/websocket/.exec(sockJS._transport.url)[1];

        stompClient.subscribe('/topic/alert-messages-user' + sessionId, function (message) {
            var currentMessage = JSON.parse(message.body);
            sleep(500).then(() => {
                pageAlert(currentMessage.message, currentMessage.type);
                if (currentMessage.type === 'danger') {
                    uploadErrors = true;
                }
            });
        });
    });
}

function pageAlert(...args) {

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
    pageAlert.addClass('h-25 d-inline-block alert-' + alertClass);
    pageAlert.text(message);
    pageAlert.show();
    if (fade) {
        pageAlert.fadeOut(5000);
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
