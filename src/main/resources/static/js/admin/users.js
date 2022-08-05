var userDetails;

function loadUserDetails(email) {
    $.ajax({
        url: '/api/admin/user',
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify({
            email: email
        }),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            userDetails = data;
            $('#form-container').show();
            $('#form-email').val(data.email);
            $('#form-display-name').val(data.displayName);
            $('#form-first-name').val(data.firstName);
            $('#form-last-name').val(data.lastName);

            $('#form-google-id').prop('checked', data.googleId);
            $('#form-enabled').prop('checked', data.enabled);
            $('#form-email-disabled').prop('checked', data.emailLoginDisabled);
            $('#form-registered-on').val(data.registeredOn);
            $('#form-last-seen').val(data.lastSeen);
            let profilePicture = $('#profile-picture');
            if (data.picture) {
                profilePicture.show();
                profilePicture.attr('src', data.picture);
            } else {
                profilePicture.hide();
            }

            // Load roles...
            let roleCheckboxesDiv = $('#form-role-checkboxes');
            let roleCheckboxHtml = '';
            for (let role of data.roles) {
                roleCheckboxHtml += '<div class="form-check form-check-inline">';
                roleCheckboxHtml +=
                    '<input class="form-check-input" type="checkbox" id="form-role-checkbox-' +
                    role.name + '" value="' + role.name + '" ';
                if (role.selected === true) {
                    roleCheckboxHtml += 'checked'
                    console.log(role.selected);
                }
                if (role.name === "ADMIN") {
                    roleCheckboxHtml += ' disabled';
                }
                roleCheckboxHtml += ' >';
                roleCheckboxHtml += '<label class="form-check-label" for="form-role-checkbox-' + role.name + '">' + role.name + '</label>';
                roleCheckboxHtml += '</div>';
            }
            roleCheckboxesDiv.html(roleCheckboxHtml);
        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, 'danger');
            } catch (e) {
                location.reload();
            }
        }
    });
}

function saveUserDetails() {
    let details_data = {
        email: $('#form-email').val(),
        displayName: $('#form-display-name').val(),
        firstName: $('#form-first-name').val(),
        lastName: $('#form-last-name').val(),
        enabled: $('#form-enabled').prop('checked'),
        emailLoginDisabled: $('#form-email-disabled').prop('checked'),
        roles: []
    };
    let roleCheckboxes = $('#form-role-checkboxes input');
    for (let i = 0; i < roleCheckboxes.length; i++) {
        if (roleCheckboxes[i].checked) {
            details_data.roles.push({
                name: roleCheckboxes[i].value,
                selected: !!roleCheckboxes[i].checked
            });
        }
    }
    $.ajax({
        url: '/api/admin/user',
        type: 'PUT',
        contentType: "application/json",
        data: JSON.stringify(details_data),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data, status, xhr) {
            if (xhr.responseURL && xhr.responseURL.includes('/user/login')) {
                location.reload();
            }
            pageAlert("Промените са запазени.", 'success', true);
            loadUserDetails(details_data.email);
        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, 'danger');
            } catch (e) {
                location.reload();
            }
        }
    });
}

function deleteUserDetails() {
    let details_data = {
        email: $('#form-email').val()
    };
    $.ajax({
        url: '/api/admin/user',
        type: 'DELETE',
        contentType: "application/json",
        data: JSON.stringify(details_data),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function () {
            location.reload();
        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, 'danger');
            } catch (e) {
                location.reload();
            }
        }
    });
}

let orderDesc = [1, 'desc'];
var table = $('#table-users').on($.fn.dataTable.ext.errMode = function (settings) {
    try {
        pageAlert(JSON.parse(settings.jqXHR.responseText).message, 'danger');
    } catch (ex) {
        location.reload();
    }
})
    .DataTable({
        stateSave: true,
        order: [orderDesc],
        columns: [
            // {name: 'userId', visible: false},
            {name: "registeredOn", className: 'text-start', searchable: false, orderable: true},
            {name: "lastSeen", className: 'text-start', searchable: false, orderable: true},
            {name: "email", className: 'text-center', searchable: true, orderable: false},
            {name: "receipts", className: 'text-end', searchable: false, orderable: false},
            {name: "total", className: 'text-end', searchable: false, orderable: false}
        ],
        serverSide: true,
        "processing": false,
        ajax: {
            url: '/api/admin/user/all',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            type: 'POST',
            data: function (d) {
                return JSON.stringify(d)
            },
            contentType: "application/json",
        }
    });

table.on('click', 'tr', function () {
    let data = table.row(this).data();
    loadUserDetails(data[2]);
});