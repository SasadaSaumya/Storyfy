//loadSingleProduct
const loadProduct = async () => {

    const response = await fetch("LoadIndexProduct");

    if (response.ok) {

        const json = await response.json();


        let productHtml = document.getElementById("similer-product");
        document.getElementById("similar-product-main").innerHTML = "";

        json.productList.forEach(item => {

            let productClone = productHtml.cloneNode(true);

            productClone.querySelector("#similer-product-image").src = "product-images/" + item.id + "/image1.png";

            productClone.querySelector("#similar-product-a1").href = "product.html?id=" + item.id;
            productClone.querySelector("#similar-product-a2").href = "product.html?id=" + item.id;

            productClone.querySelector("#similar-title").innerHTML = item.title;
            productClone.querySelector("#similar-author").innerHTML = item.author.name;

            productClone.querySelector("#similar-price").innerHTML = new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                }
            ).format(item.price);

            productClone.querySelector("#similar-add-to-cart").addEventListener(
                "click",
                (e) => {
                    addToCart(item.id, 1);
                    e.preventDefault();
                });


            console.log(productClone);

            const similarProductMain = document.getElementById("similar-product-main");
            similarProductMain.appendChild(productClone);

        });



    } else {
        window.location = "index.html";
    }




};

const addToCart = async (id, qty) => {
    console.log("id " + id + " " + qty);

    const popup = Notification();

    popup.setProperty({

        isHidePrev: true

    });


    const response = await fetch(
        "AddToCart?id=" + id + "&qty=" + qty,
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {

            popup.success({
                title: 'Success',
                message: json.content
            });

        } else {
            popup.error({
                title: 'Error',
                message: json.content
            });


        }


    } else {
        popup.error({
            title: 'Error',
            message: "Please try again later"
        });

    }


};

