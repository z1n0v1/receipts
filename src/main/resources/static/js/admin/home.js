let orderDesc = [1, 'desc'];
var table = $('#receipts-table').on($.fn.dataTable.ext.errMode = function (settings) {
    let alert = $('#alert');
    try {
        alert.text(JSON.parse(settings.jqXHR.responseText).message);
    } catch (ex) {
        location.reload();
    }
    alert.show();
    alert.fadeOut(8000);
}).DataTable({
    "bFilter": false,
    stateSave: true,
    order: [orderDesc],
    columns: [
        {name: 'receiptId', visible: false},
        {name: "addedOn", className: 'text-start', searchable: false},
        {name: "addedBy", className: 'text-start', searchable: false, orderable: false},
        {name: "isProcessed", className: 'text-center', searchable: false},
        {name: "total", className: 'text-end', searchable: false, orderable: false},
        {name: "itemsTotal", className: 'text-end', searchable: false, orderable: false}
    ],
    serverSide: true,
    "processing": false,
    ajax: {
        url: '/api/admin/receipt/all',
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

table.on('click', 'tr', function () {
    let data = table.row(this).data();
    window.location.href = '/admin/receipt/' + data[0];
});
