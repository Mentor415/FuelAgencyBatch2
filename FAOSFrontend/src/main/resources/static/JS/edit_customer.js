document.getElementById('editForm').addEventListener('submit', function(event) {
    event.preventDefault();  // Prevent form from submitting normally
    const custId = document.querySelector('input[name="cust_id"]').value;

    // Redirect to Edit_Details.html with cust_id as a request parameter
    window.location.href = `/edit?cust_id=${custId}`;
});

document.addEventListener("DOMContentLoaded", () => {
        // Get the URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        const consumerId = urlParams.get('consumerId'); // Extract 'consumerId' parameter

        // If 'consumerId' exists, set it in the search box
        if (consumerId) {
            const searchBox = document.getElementById("searchInput"); // Ensure the search input has this ID
            searchBox.value = consumerId; // Pre-fill the search box with the customer ID
        }
});
/*<script>
        function activateCustomer() {
            if (confirm("Are you sure you want to activate this customer?")) {
                fetch('/activate/' + document.getElementById('consumerId').value, { method: 'POST' })
                    .then(response => {
                        if (response.ok) {
                            window.location.reload();
                        }
                    });
            }
        }
    </script>-->*/