let categoriesData = {};

$(document).ready(function () {

    $.ajax({
        url: '/api/admin/category/all',
        type: 'GET',
        dataType: 'json',
        contentType: "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            let html = '';

            for (let i = 0; i < data.length; i++) {
                html += '<div class="col-lg-2 col-md-3 col-4 m-2 border text-center"' +
                    ' onclick="editCategory(' + "'" + data[i].id + "')" + '">';
                html += '<strong>' + data[i].name + ' </strong>';
                html += '<span>(' + data[i].itemsCount + ')</span>';
                html += '<div><input type="color" value="' + data[i].color + '" disabled></div>';
                html += '</div>';

                categoryData = {
                    id: data[i].id,
                    name: data[i].name,
                    color: data[i].color,
                    itemsCount: data[i].itemsCount
                }
                categoriesData[data[i].id] = categoryData;
            }
            $('#categories').append(html);
        },
        error: function (request) {
            try {
                pageAlert(JSON.parse(request.responseText).message, "danger");
            } catch (e) {
                location.reload();
            }
        }
    });

});

$('.category-edit-modal-close').on('click', function () {
    $('#category-edit-modal').modal('toggle');
});

$('.category-add-modal-close').on('click', function () {
    $('#category-add-modal').modal('toggle');
});

function saveCategory() {
    let category = {
        id: $('#category-edit-id').val(),
        name: $('#category-edit-name').val(),
        color: $('#category-edit-color').val()
    };

    $.ajax({
        url: '/api/admin/category',
        type: 'PUT',
        dataType: 'json',
        contentType: "application/json",
        data: JSON.stringify(category),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function () {
            location.reload();
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "danger", false, '#modal-edit-alert');
            } catch (e) {
                location.reload();
            }
        }
    });
}

function editCategory(id) {
    $('#category-edit-id').val(id);
    $('#category-edit-name').val(categoriesData[id].name);
    $('#category-edit-color').val(categoriesData[id].color);

    if (categoriesData[id].itemsCount === 0) {
        $('#category-edit-delete-btn').show();
    } else {
        $('#category-edit-delete-btn').hide();
    }

    $('#category-edit-modal').modal('show');
}

function addCategory() {
    let category = {
        name: $('#category-add-name').val(),
        color: $('#category-add-color').val()
    };

    $.ajax({
        url: '/api/admin/category',
        type: 'POST',
        dataType: 'json',
        contentType: "application/json",
        data: JSON.stringify(category),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function () {
            location.reload();
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "danger", false, "#alert-modal-category-add");
            } catch (e) {
                location.reload();
            }
        }
    });
}

function deleteCategory() {
    let category = {
        id: $('#category-edit-id').val()
    };

    $.ajax({
        url: '/api/admin/category',
        type: 'DELETE',
        dataType: 'json',
        contentType: "application/json",
        data: JSON.stringify(category),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader,csrfToken);
        },
        success: function () {
            location.reload();
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "danger", false, "#modal-edit-alert");
            } catch (e) {
                location.reload();
            }
        }
    });
}

function addCategoryModal() {
    $('#category-add-modal').modal('show');
}