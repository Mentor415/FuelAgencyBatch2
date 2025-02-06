function searchCustomer() {
    const searchInput = document.getElementById("searchInput").value.trim();
    const rows = document.querySelectorAll("#customerTableBody tr");
    const messageContainer = document.getElementById("messageContainer");
    let found = false; // To track if any match is found

    rows.forEach(row => {
        const consumerId = row.cells[1].innerText.trim();
        if (consumerId === searchInput) {
            row.style.display = ""; // Show the row
            found = true; // Match found
        } else {
            row.style.display = "none"; // Hide the row
        }
    });

    if (!found && searchInput) {
        messageContainer.style.display = "block"; // Show the message container
        messageContainer.textContent = "Customer not found";

        // Hide the message after 5 seconds
        setTimeout(() => {
            messageContainer.style.display = "none";
        }, 3000);

        rows.forEach(row => {
            row.style.display = ""; // Show all rows
        });
    } else {
        messageContainer.style.display = "none"; // Hide the message container if a match is found
    }

    updateSerialNumbers(); // Update the serial numbers after filtering
}



document.addEventListener("DOMContentLoaded", () => {
    // Initially hide the deactivation and activation columns
    const deactivationColumns = document.querySelectorAll('.deactivation-column');
    const activationColumns = document.querySelectorAll('.activation-column');
    [...deactivationColumns, ...activationColumns].forEach(col => col.style.display = "none");

    // Apply the default filter logic for "all"
    filterCustomers();
});

function filterCustomers() {
    const filterValue = document.getElementById("filterDropdown").value;

    // Select all rows in the table body
    const rows = document.querySelectorAll("#customerTableBody tr");

    // Select deactivation and activation columns
    const deactivationColumns = document.querySelectorAll('.deactivation-column');
    const activationColumns = document.querySelectorAll('.activation-column');

    // Show/Hide Deactivation and Activation Columns Based on Filter
    const showDeactivationColumns = filterValue === "deactivated";
    const showActivationColumns = filterValue === "active";
    deactivationColumns.forEach(col => col.style.display = showDeactivationColumns ? "" : "none");
    activationColumns.forEach(col => col.style.display = showActivationColumns ? "" : "none");

    rows.forEach(row => {
        const status = row.cells[8].innerText.trim().toLowerCase(); // Adjust cell index as needed for status
        
        if (filterValue === "all" || filterValue === "byDefault") {
            row.style.display = "";
        } else if (filterValue === "active" && status === "true") {
            row.style.display = "";
        } else if (filterValue === "deactivated" && status === "false") {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });

    updateSerialNumbers();
}

function updateSerialNumbers() {
    const rows = document.querySelectorAll("#customerTableBody tr");
    let srNo = 1;
    rows.forEach(row => {
        if (row.style.display !== "none") {
            row.cells[0].innerText = srNo++;
        }
    });
}

function activateCustomer(consumerId) {
    if (confirm(`Are you sure you want to activate customer with ID ${consumerId}?`)) {
        fetch('/activateCustomer/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ consumerId }),
        })
            .then(response => {
                if (response.ok) {
                    alert('Customer activated successfully.');
                    location.reload();
                } else {
                    alert('Failed to activate customer. Please try again.');
                }
            })
            .catch(error => {
                alert(`Error: ${error.message}`);
            });
    }
}

function deleteCustomer(consumerId) {
    if (confirm(`Are you sure you want to delete the customer with ID ${consumerId}?`)) {
        fetch(`/deleteCustomer/`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ consumerId }),
        })
            .then(response => {
                if (response.ok) {
                    alert('Customer deleted successfully.');
                    location.reload(); // Reload the page to reflect changes
                } else {
                    response.text().then(text => {
                        alert(`Failed to delete customer: ${text}`);
                    });
                }
            })
            .catch(error => {
                alert(`Error: ${error.message}`);
            });
    }
}
