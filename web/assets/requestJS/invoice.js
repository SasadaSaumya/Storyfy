//printInvoice
const printInvoice = () => {
    const body = document.body.innerHTML;
    const page = document.getElementById("page").innerHTML;
    document.body.innerHTML = page;
    window.print();
    document.body.innerHTML = body;
}

//load invoice data
const loadInvoice = async () => {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("orderId")) {

        const productId = parameters.get("orderId");

        const response = await fetch("LoadInvoiceData?orderId=" + productId);

        if (response.ok) {

            const json = await response.json();

            const orderItemsList = json.orderItemsList;

            const orderData = json.orderData

            console.log(orderItemsList);


            let invoiceTableMain = document.getElementById("invoiceTableMain");
            let itemRow = document.getElementById("itemRow");

            invoiceTableMain.innerHTML = "";

            let totalQty = 0;
            let total = 0;

            orderItemsList.forEach(item => {

                let itemSubTotal = (item.product.price * item.qty);

                totalQty += item.qty;
                total += itemSubTotal;

                let itemRowClone = itemRow.cloneNode(true);
                itemRowClone.querySelector("#id").innerHTML = item.product.id;
                itemRowClone.querySelector("#p_name").innerHTML = item.product.title;
                itemRowClone.querySelector("#qty").innerHTML = item.qty;
                itemRowClone.querySelector("#price").innerHTML = item.product.price;


                invoiceTableMain.appendChild(itemRowClone);
            });

            document.getElementById("invoiceNum").innerHTML = orderData.id;
            document.getElementById("datetime").innerHTML = orderData.date_time;
            document.getElementById("userName").innerHTML = orderData.user.first_name;

            document.getElementById("email").innerHTML = orderData.user.email;
            document.getElementById("mobile").innerHTML = orderData.address.mobile;
            document.getElementById("address").innerHTML = orderData.address.line1 + " " + orderData.address.line2 + " " + orderData.address.postal_code + " " + orderData.address.city.name;


            var shipping_amount = 0;

            if (orderData.address.city.name == "colombo") {

                //colombo
                shipping_amount = 350;

            } else {

                //out of colombo
                shipping_amount = 500;
            }

            document.getElementById("subTotal").innerHTML = total;
            document.getElementById("shipping").innerHTML = shipping_amount;
            total += shipping_amount;
            document.getElementById("total").innerHTML = total;




        }
    }


}
