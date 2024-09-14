//loadSingleProduct
const loadProduct = async () => {

    const response = await fetch("LoadIndexProduct");

    if (response.ok) {

        const json = await response.json();

        console.log(json);

        loadProductCards(json.productList, "similar");

        loadProductCards(json.productListDate, "dateP");

        loadProductCards(json.productListThriller, "Thriller");




    } else {
        window.location = "index.html";
    }




};

const loadProductCards = (dataList, prefix) => {
    let productHtml = document.getElementById(prefix + "-product");
    document.getElementById(prefix + "-product-main").innerHTML = "";

    dataList.forEach(item => {

        let productClone = productHtml.cloneNode(true);

        productClone.querySelector("#" + prefix + "-product-image").src = "product-images/" + item.id + "/image1.png";

        productClone.querySelector("#" + prefix + "-product-a1").href = "product.html?id=" + item.id;
        productClone.querySelector("#" + prefix + "-product-a2").href = "product.html?id=" + item.id;

        productClone.querySelector("#" + prefix + "-title").innerHTML = item.title;
        productClone.querySelector("#" + prefix + "-author").innerHTML = item.author.name;

        productClone.querySelector("#" + prefix + "-price").innerHTML = new Intl.NumberFormat(
            "en-US",
            {
                minimumFractionDigits: 2
            }
        ).format(item.price);

        productClone.querySelector("#" + prefix + "-add-to-cart").addEventListener(
            "click",
            (e) => {
                addToCart(item.id, 1);
                e.preventDefault();
            });


        console.log(productClone);

        const similarProductMain = document.getElementById(prefix + "-product-main");
        similarProductMain.appendChild(productClone);

    });

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

