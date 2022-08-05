
$('#role-add-modal').click(function () {
    let newRole = {
        name: $('#modal-role-name').val(),
        capabilities: []
    };
    if (!newRole.name || newRole.name.length < 3) {
        $('#modal-page-alert').text('Името на ролята трябва да е поне 3 символа').show();
        return;
    }
    let capabilities = $('#modal-capabilities-list span');
    for (let i = 0; i < capabilities.length; i++) {
        if (capabilities[i].classList.contains('bg-primary')) {
            newRole.capabilities.push(
                {
                    name: capabilities[i].dataset.capability,
                }
            );
        }
    }
    $.ajax('/api/admin/role', {
        method: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        data: JSON.stringify(newRole),
        contentType: 'application/json',
        success: function () {

            location.reload();

        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, "danger", false, "#modal-page-alert");
            } catch (e) {
                location.reload();
            }
        }
    });
});

$('#add-role-button').click(function () {

    // load capabilities

    let myerror = false;

    $.ajax({
        url: '/api/admin/capability/all',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        contentType: 'application/json',
        xhr: () => new XMLHttpRequest(),
        success: function (data, status, xhr) {
            if (!xhr.responseJSON) {
                location.reload();
            }
            console.log(xhr.responseURL, xhr.getResponseHeader('Location'),xhr);
            var capabilities = data;
            var capabilities_html = '';
            for (let capabilities_data of capabilities) {

                capabilities_html +=
                    '<span onclick="roleAddCapabilityClick(this);" class="btn badge mx-2 ';
                if (capabilities_data.active) {
                    capabilities_html += 'bg-primary" data-active="true"';
                } else {
                    capabilities_html += 'bg-secondary"  data-active="false"';
                }
                capabilities_html += ' data-capability="' + capabilities_data.capability + '"';
                if (capabilities_data.description) {
                    capabilities_html += ' title="' + capabilities_data.description + '"'
                }
                capabilities_html += '> ' +
                    capabilities_data.description;
                capabilities_html += ' </span>'
            }

            $('#modal-capabilities-list').html(capabilities_html);
        },
        error: function () {
            try {
                pageAlert(request.responseJSON.message, "danger");
            } catch (e) {
                location.reload();
            }
            myerror = true;
        }
    });

    if(!myerror){
        $('#addRoleModal').modal('show');
    }
});

$('.add-role-modal-close').click(function () {
    $('#addRoleModal').modal('toggle');
});

$(document).ready(function () {
    loadRoles();
});

var myData;

function deleteRole(role) {
    $.ajax('/api/admin/role', {
        method: 'DELETE',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        data: JSON.stringify({
            name: role
        }),
        contentType: 'application/json',
        success: function () {
            location.reload();
        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, "danger");
            } catch (e) {
                location.reload();
            }
        }
    });
}

function loadRoles() {
    $.ajax({
        url: '/api/admin/role/all',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            myData = data;
            let roles = $('#roles');
            let role_html = '';

            for (const role_data of data) {
                role_html +=
                    '<div class="col-xxl-4 col-xl-4 col-lg-6 col-md-12 col-sm-12">' +
                    '<div class="card-header">' +
                    "<button class='btn-sm btn-primary m-1' type='button' onclick=saveRole('" +
                    role_data.role +
                    "')>Запиши</button>" +
                    '<button class="btn-sm btn-danger m-1" type="button" onclick="deleteRole(\'' +
                    role_data.role +
                    '\')">Изтрий</button>' +
                    '<h5>' + role_data.role + '</h5>';
                if (role_data.description) {
                    role_html += ' (' + role_data.description + ')';
                }
                role_html +=
                    '</div>'
                '<div class="">'
                for (const capabilities_data of role_data.capabilities) {
                    role_html +=
                        '<span onclick="capabilityClick(this);" class="btn badge mx-2 ';
                    if (capabilities_data.active) {
                        role_html += 'bg-primary" data-active="true"';
                    } else {
                        role_html += 'bg-secondary"  data-active="false"';
                    }
                    role_html += ' data-capability="' + capabilities_data.capability + '"';
                    role_html += ' data-role="' + role_data.role + '"'
                    if (capabilities_data.description) {
                        role_html += ' title="' + capabilities_data.description + '"'
                    }
                    role_html += '> ';
                    if (capabilities_data.description) {
                        role_html += capabilities_data.description;
                    } else {
                        role_html += capabilities_data.capability;
                    }

                    role_html += ' </span>'
                }
                role_html +=
                    '</div>' +
                    '</div>' +
                    '</div>';
            }
            roles.html(role_html);
        },
        error: function (data) {
            try {
                pageAlert(data.responseJSON.message, "danger");
            } catch (e) {
                location.reload();
            }
        }
    });
}

function saveRole(role) {

    if (role === "ADMIN") {
        pageAlert("Не може да променяте ролята ADMIN", "danger");
        return;
    }

    for (const role_data of myData) {
        if (role_data.role === role) {
            $.ajax({
                url: '/api/admin/role',
                type: 'PUT',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                data: JSON.stringify(role_data),
                contentType: "application/json",
                success: function (data, status, xhr) {
                    if (!xhr.responseJSON) {
                        location.reload();
                    }
                    pageAlert("Ролята бе записана успешно.", "success");
                },
                error: function (data) {
                    try {
                        pageAlert(data.responseJSON.message, "danger");
                    } catch (e) {
                        location.reload();
                    }
                }

            });
        }
    }
}

function roleAddCapabilityClick(e) {
    const capability = e.dataset.capability;
    const active = e.dataset.active;

    if (active === "true") {
        e.dataset.active = false;
        e.classList.remove('bg-primary');
        e.classList.add('bg-secondary');
        changeCapability(role, capability, false);
    } else {
        e.dataset.active = true;
        e.classList.add('bg-primary');
        e.classList.remove('bg-secondary');
    }
}

function capabilityClick(e) {
    const capability = e.dataset.capability;
    const active = e.dataset.active;
    const role = e.dataset.role;

    if (role === "ADMIN") {
        pageAlert("Не може да променяте ролята ADMIN", "danger");
        return;
    }

    if (active === "true") {
        e.dataset.active = false;
        e.classList.remove('bg-primary');
        e.classList.add('bg-secondary');
        changeCapability(role, capability, false);
    } else {
        e.dataset.active = true;
        e.classList.add('bg-primary');
        e.classList.remove('bg-secondary');
        changeCapability(role, capability, true);
    }
}

function changeCapability(role, capability, isActive) {
    for (let i = 0; i < myData.length; i++) {
        if (myData[i].role === role) {
            for (let j = 0; j < myData[i].capabilities.length; j++) {
                if (myData[i].capabilities[j].capability === capability) {
                    myData[i].capabilities[j].active = isActive;
                }
            }
        }
    }
}