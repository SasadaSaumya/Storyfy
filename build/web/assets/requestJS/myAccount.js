//load add product nav => product features 

const loadProductFeatures = async () => {

    const response = await fetch("LoadProductFeatures");


    if (response.ok) {
        const json = await response.json();

        console.log(json);

        loadSelectTag("category", json.categoryList, "name");
        loadSelectTag("author", json.authorList, "name");
        loadSelectTag("publisher", json.publisherList, "name");
        loadSelectTag("status", json.statusList, "name");



    } else {
        console.log("Something went wrong");

    }


};

const loadSelectTag = (selectTagId, list, property) => {

    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item[property];
        selectTag.appendChild(optionTag);
    });

};