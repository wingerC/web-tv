const renderCounter = () => {
	let fcounter = JSON.parse(localStorage.getItem("webtv-fav")).length;
	document.getElementById("fav-counter").innerHTML = `<sub>${fcounter}</sub>`;
};
