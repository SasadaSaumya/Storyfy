//loadSingleProduct
const loadProduct = async () => {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {

        const productId = parameters.get("id");

        const response = await fetch("LoadSingleProduct?id=" + productId);

        if (response.ok) {

            const json = await response.json();

            const id = json.product.id;

            console.log(json);

            document.getElementById("image1").src = "product-images/" + id + "/image1.png";

            document.getElementById("product-title").innerHTML = json.product.title;
            document.getElementById("product-price").innerHTML = new Intl.NumberFormat().format(json.product.price);

            document.getElementById("author").innerHTML = json.product.author.name;
            document.getElementById("author-description").innerHTML = json.product.author.description;

            document.getElementById("publisher").innerHTML = json.product.publisher.name;

            document.getElementById("categoryName").innerHTML = json.product.category.name;

            document.getElementById("product_des").innerHTML = json.product.description

            document.getElementById("isbn").innerHTML = json.product.isbn;


            document.getElementById("published_date").innerHTML = json.product.published_date;
            document.getElementById("pages").innerHTML = json.product.page;


            document.getElementById("add-to-cart-main").addEventListener(
                "click",
                (e) => {
                    addToCart(
                        json.product.id,
                        document.getElementById("addToCartQty").value
                    );
                    e.preventDefault();
                }
            );

            document.getElementById("buy-now-main").addEventListener(
                "click",
                (e) => {
                    addToCart(
                        json.product.id,
                        document.getElementById("addToCartQty").value
                    );
                    window.location = "cart.html";
                    e.preventDefault();
                }
            );


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