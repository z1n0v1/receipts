let companyEik = $('#company-eik');
let storeName = $('#store-name');
let storeAddress = $('#store-address');
let receiptDate = $('#receipt-date');
let receiptTotal = $('#receipt-total');
let receiptEditBtn = $('#receipt-edit-btn');
let receiptSaveBtn = $('#receipt-save-btn');
let receiptCancelBtn = $('#receipt-cancel-btn');

let receiptData = {};

receiptSaveBtn.on('click', function () {
    $.ajax({
        url: '/api/receipt',
        type: 'PUT',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        contentType: 'application/json',
        data: JSON.stringify({
            id: receiptData.receiptId,
            eik: $('#edit-company-eik').val(),
            name: $('#edit-store-name').val(),
            address: $('#edit-store-address').val(),
            date: $('#edit-receipt-date').val(),
            total: $('#edit-receipt-total').val()
        }),
        success: function () {
            location.reload();
        },
        error: function (response) {
            try {
                pageAlert(response.responseJSON.message, "bg-danger");
            } catch (e) {
                location.reload();
            }
        }
    });
});

receiptCancelBtn.on('click', function () {

    receiptCancelBtn.hide();
    receiptSaveBtn.hide();

    companyEik.text(receiptData.companyEik);
    storeName.text(receiptData.storeName);
    storeAddress.text(receiptData.storeAddress);
    receiptDate.text(receiptData.receiptDateText);
    receiptTotal.text(receiptData.receiptTotal);

    receiptEditBtn.show();
});

receiptEditBtn.on('click', function () {
    receiptData = {
        receiptId: thisReceiptId,
        companyEik: companyEik.text(),
        storeName: storeName.text(),
        storeAddress: storeAddress.text(),
        receiptDate: receiptDate.data('date'),
        receiptDateText: receiptDate.text(),
        receiptTotal: receiptTotal.text()
    };

    receiptEditBtn.hide();
    receiptSaveBtn.show();
    receiptCancelBtn.show();

    let companyEik_html = '';
    companyEik_html += '<button class="btn-sm btn-secondary" id="eik-reload">'
    companyEik_html += '<img src="/images/reload.svg" width="16" height="16"';
    companyEik_html += ' class="reload-icon" id="reload-company-eik" alt="ЕИК"></button>';

    companyEik_html += '<input type="text" class="form-control" ';
    companyEik_html += 'id="edit-company-eik" placeholder="ЕИК" value="' + receiptData.companyEik + '">';
    companyEik.html(companyEik_html);

    storeName.html(`<input type="text" class="form-control" id="edit-store-name" value="`
        + receiptData.storeName +`">`);
    storeAddress.html(`<input type="text" class="form-control" id="edit-store-address" value='`
        + receiptData.storeAddress +`'>`);
    receiptDate.html(`<input type="datetime-local" class="form-control" id="edit-receipt-date" value="`
        + receiptData.receiptDate +`">`);
    receiptTotal.html(`<input type="number" class="form-control" id="edit-receipt-total" value="`
        + receiptData.receiptTotal +`">`);

    $('#eik-reload').on('click', function () {
        $.ajax({
            url: '/api/company/eik',
            type: 'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            contentType: 'application/json',
            data: JSON.stringify({
                eik: $('#edit-company-eik').val()
            }),
            success: function (data) {
                $('#receipt-company-name').text(data.companyName);
                $('#receipt-company-address').text(data.companyAddress);
            },
            error: function (data) {
                try {
                    pageAlert(data.responseJSON.message, "bg-danger");
                } catch (e) {
                    location.reload();
                }
            }
        });
    });
});


$('#item-add-btn').click(function () {
    let categories = [];
    let myerror = false;

    $.ajax({
        url: '/api/category/all',
        type: 'GET',
        contentType: "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {

            categories = data;
            $('#add-item-category').empty();
            $.each(categories, function (index, value) {
                $('#add-item-category').append('<option value="' + value.name + '">' + value.name + '</option>');
            });
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "bg-danger", false, "#alert-modal-edit-item");
            } catch (e) {
                location.reload();
            }
            myerror = true;
        }

    });

    if (!myerror) {
        $('#addItemModal').modal('show');
    }
});

$('.add-item-modal-close').click(function () {
    $('#addItemModal').modal('toggle');
});

$('#add-item-form').submit(function (e) {

    e.preventDefault();

    $.ajax({
        type: 'POST',
        url: '/api/item',
        data: JSON.stringify({
            receiptId: $('#add-item-receipt-id').val(),
            category: $('#add-item-category').val(),
            name: $('#add-item-name').val(),
            quantity: $('#add-item-quantity').val(),
            price: $('#add-item-price').val()
        }),
        contentType: "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function () {
            location.reload();
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "bg-danger", false, "#alert-modal-item-add");
            } catch (e) {
                location.reload();
            }
        }
    });
});

var table = $('#items-table').on($.fn.dataTable.ext.errMode = function (settings) {
    try {
        pageAlert(settings.jqXHR.responseJSON.message, "bg-danger");
    } catch (ex) {
        location.reload();
    }
}).DataTable({
    stateSave: true,
    columns: [
        {name: "position", className: 'text-start', searchable: false},
        {name: "category", className: 'text-start', searchable: true},
        {name: "name", className: 'text-start', width: '60%', searchable: true},
        {
            name: "quantity", className: 'text-end', render: DataTable.render.number(null, null, 3, null),
            searchable: false
        },
        {
            name: "price", className: 'text-end', render: DataTable.render.number(null, null, 2, null, ' лв.'),
            searchable: false
        }
    ],
    serverSide: true,
    "processing": false,
    ajax: {
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        url: '/api/item/' + thisReceiptId,
        type: 'POST',
        data: function (d) {
            return JSON.stringify(d)
        },
        contentType: "application/json",
    }
});

table.on('click', 'tr', function () {
    var data = table.row(this).data();

    $('#edit-item-position').val(data[0]);
    $('#edit-item-category').val(data[1]);
    $('#edit-item-name').val(data[2]);
    $('#edit-item-quantity').val(data[3]);
    $('#edit-item-price').val(data[4]);

    $.ajax({
        url: '/api/category/all',
        type: 'GET',
        dataType: 'json',
        contentType: "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (cdata) {
            categories = cdata;
            console.log(categories);
            $('#edit-item-category').empty();
            $.each(categories, function (index, value) {
                let catOption = '<option value="' + value.name + '" ';
                if (value.name === data[1]) {
                    catOption += 'selected';
                }
                catOption += ' >' + value.name + '</option>';

                $('#edit-item-category').append(catOption);
            });
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "bg-danger", false, "#alert-modal-edit-item");
            } catch (e) {
                location.reload();
            }
        }
    });

    $('#editItemModal').modal('toggle');
});

$('.edit-item-modal-close').click(function () {
    $('#editItemModal').modal('toggle');
});

$('#edit-item-delete').click(() => {
    let position = $('#edit-item-position').val();
    let receiptId = $('#edit-item-receipt-id').val();
    $.ajax({
        url: '/api/item',
        contentType: "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        data: JSON.stringify({
            position: position,
            receiptId: receiptId
        }),
        type: 'DELETE',
        success: function () {
            location.reload();
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "bg-danger", false, "#alert-modal-edit-item");
            } catch (e) {
                location.reload();
            }
        }
    });
});

$('#edit-item-form').submit((e) => {
    e.preventDefault();

    $.ajax({
        type: 'PUT',
        url: '/api/item',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        data: JSON.stringify({
            receiptId: $('#edit-item-receipt-id').val(),
            category: $('#edit-item-category').val(),
            position: $('#edit-item-position').val(),
            name: $('#edit-item-name').val(),
            quantity: $('#edit-item-quantity').val(),
            price: $('#edit-item-price').val()
        }),
        contentType: "application/json",
        success: function () {
            location.reload();
        },
        error: function (request) {
            try {
                pageAlert(request.responseJSON.message, "bg-danger", false, "#alert-modal-edit-item");
            } catch (e) {
                location.reload();
            }
        }
    });
});

var map;
var service;
var infowindow;

function initMap() {
    infowindow = new google.maps.InfoWindow();

    var map = new google.maps.Map(document.getElementById('map'));
    var request = {
        query: receiptStoreAddress,
        fields: ['name', 'geometry'],
    };

    var service = new google.maps.places.PlacesService(map);

    service.findPlaceFromQuery(request, function (results, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
            map = new google.maps.Map(
                document.getElementById('map'), {center: results[0].geometry.location, zoom: 17});
            map.setCenter(results[0].geometry.location);
            new google.maps.Marker({
                position: results[0].geometry.location,
                map: map
            });
            createMarker(results[0]);
        }
    });
}

function createMarker(place) {
    new google.maps.Marker({
        position: place.geometry.location,
        map: map
    });
}
