$(document).ready(function () {

    $.ajax({
        url: '/api/home/expenses/last-month/by-week/line',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            let ctx = document.getElementById('last-month-expenses-by-week');
            new Chart(ctx, data);
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

    $.ajax({
        url: '/api/home/expenses/categories/last-month/pie',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            let ctx = document.getElementById('monthly-expenses-by-type');
            new Chart(ctx, data);
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

    $.ajax({
        url: '/api/home/expenses/categories/total/pie',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            let ctx = document.getElementById('total-expenses-by-type');
            new Chart(ctx, data);
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

    $.ajax({
        url: '/api/home/statistics',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader,csrfToken);
        },
        success: function (data) {
            $('#last-month-expenses').text(data.lastMonthExpenses);
            $('#total-expenses').text(data.totalExpenses);
            $('#last-month-receipts').text(data.lastMonthReceipts);
            $('#total-receipts').text(data.totalReceipts);
            $('#total-sellers').text(data.numCompanies);
            $('#total-stores').text(data.numStores);
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

    })
});