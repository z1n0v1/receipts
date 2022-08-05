function file_upload() {
    let upBtn = $('#btn-upload');
    upBtn.disabled = true;
    let spinner = $('#spinner-upload');
    spinner.removeClass('d-none');

    let xhr = new XMLHttpRequest();
    let data = new FormData();

    input = document.getElementById('customFile');
    if (!input.value) {

        pageAlert("Не сте избрали касови бележки!", "danger");

        upBtn.disabled = false;
        spinner.addClass('d-none');
        return;
    }
    for (let f of input.files) {
        data.append('file', f);
    }
    xhr.open("post", "/api/receipt");
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.send(data);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                if (xhr.responseURL.includes('/user/login')) {
                    location.reload();
                    return;
                }
                sleep(4000).then(() => {
                    if (uploadErrors) {
                        pageAlert('Има грешки при качването на касовите бележки!',
                            'warning', true);
                        uploadErrors = false;
                    } else {
                        pageAlert('Качването на касовите бележки е успешно!', 'success', true);
                    }
                    spinner.css('display', 'none');
                });
                upBtn.disabled = false;
                spinner.css('display', 'none');
            } else{
                try {
                    pageAlert(JSON.parse(xhr.responseText).message, "danger", true);
                } catch (e) {
                    location.reload();
                }
                upBtn.disabled = false;
                spinner.css('display', 'none');
            }
        }
    };
}