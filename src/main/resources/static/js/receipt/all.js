var receiptDeleteAction = false;

let orderDesc = [1, 'desc'];
var table = $('#receipts-table').on($.fn.dataTable.ext.errMode = function (settings) {
    try {
        pageAlert(JSON.parse(settings.jqXHR.responseText).message, 'danger');
    } catch (ex) {
        location.reload();
    }
}).DataTable({
    "pageLength": 25,
    // "bFilter": false,
    stateSave: true, // Should we save the state of the table ?
    order: [orderDesc],
    columns: [
        {name: 'receiptId', visible: false, searchable: false, orderable: false},
        {name: "dateOfPurchase", className: 'text-start', searchable: false, orderable: true},
        {name: "total", className: 'text-end', searchable: false, orderable: true},
        {name: "categories", className: 'text-center', searchable: true, orderable: false},
        {name: "companyName", className: 'text-start', searchable: true, orderable: false},
        {name: "storeName", className: 'text-start', searchable: true, orderable: false,},
        {
            name: "delete", className: 'text-center', searchable: false, orderable: false,
            render: function (data, type, row) {
                let btn;
                /*[# sec:authorize="hasAuthority('CAP_DELETE_RECEIPT')"]*/
                btn = '<button class="btn btn-danger btn-sm" onclick="deleteReceipt('
                    + "'" + row[0] + "'" + ')">Изтрий</button>';
                /*[/]*/
                return btn;
            }
        },
    ],
    serverSide: true,
    "processing": false,
    ajax: {

        url: '/api/receipt/list',
        type: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        data: function (d) {
            return JSON.stringify(d)
        },
        contentType: "application/json",
    }

});


function deleteReceipt(id) {
    $.ajax({
        url: '/api/receipt',
        type: 'DELETE',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader,csrfToken);
        },
        data: JSON.stringify({
            receiptId: id
        }),
        contentType: "application/json",
        success: function () {
            pageAlert("Касовата бележка бе изтрита успешно", 'success', true);
            receiptDeleteAction = true;
            table.ajax.reload();
        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, 'danger');
                receiptDeleteAction = true;
            } catch (ex) {
                pageAlert('Нещо се обърка. Моля опитайте отново.', 'danger');
                receiptDeleteAction = true;
            }

        }
    });
}

table.on('click', 'tr', function (e) {
    let data = table.row(this).data();
    if (e.target.textContent === 'Изтрий') {
        receiptDeleteAction = false;
    } else {
        window.location.href = '/receipt/details/' + data[0];
    }
});
