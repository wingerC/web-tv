let movies;
const STORAGE_FAV = "webtv-fav";

document.getElementById("search-button").addEventListener("click", searchReq);
document.getElementById("form-id").addEventListener("submit", searchReq);

let store = localStorage.getItem(STORAGE_FAV);
if (store) {
	fetchData(store);
	document.getElementById("h3-list").style.display = "none";
} else {
}

let storeArray = JSON.parse(store);

const renderCounter = () => {
	let fcounter = JSON.parse(localStorage.getItem(STORAGE_FAV)).length;
	document.getElementById("fav-counter").innerHTML = `<sub>${fcounter}</sub>`;
};

renderCounter();

function render(data) {
	if (data.length == 0) {
		document.getElementById("foot").style.display = "block";
		return;
	}
	movies = data;

	const storeArray = JSON.parse(localStorage.getItem(STORAGE_FAV)) ?? [];
	const list = document.getElementById("list");
	//const output = document.createElement("div");

	data.forEach((movie) => {
		let srcPost = movie.description.src;

		const card = document.createElement("div");
		card.className = "card";
		card.id = movie.id;

		const cardRuTitle = document.createElement("h3");
		cardRuTitle.className = "card-h3";
		cardRuTitle.textContent = movie.ruTitle;

		card.appendChild(cardRuTitle);

		const cardEnTitle = document.createElement("p");
		cardEnTitle.className = "card-p";
		cardEnTitle.textContent = movie.enTitle;

		card.appendChild(cardEnTitle);

		const cardPoster = document.createElement("img");
		cardPoster.className = "card-poster";
		cardPoster.setAttribute("src", srcPost);
		cardPoster.setAttribute("width", 200);
		cardPoster.setAttribute("height", 250);

		card.appendChild(cardPoster);

		const cardDescription = document.createElement("div");
		cardDescription.className = "descr";

		/* const cardPs = {
			"Год: ": movie.year,
			"Размер: ": movie.size,
			"Пиры: ": movie.peers,
			"Релиз: ": movie.date,
		};
		for (const [key, value] of Object.entries(cardPs)) {
			const p = document.createElement("p");
			const span = document.createElement("span");
			span.className = "card-info";
			span.textContent = key;
			p.appendChild(span);
			p.textContent += value;
			cardDescription.appendChild(p);
		} */

		const p = document.createElement("p");
		p.append(movie.description.info);
		cardDescription.appendChild(p);

		const cardBtnHolder = document.createElement("div");
		cardBtnHolder.className = "btn-holder";

		const cardBtnWatch = document.createElement("button");
		cardBtnWatch.id = "watch";
		cardBtnWatch.className = "btn-card";
		cardBtnWatch.setAttribute("type", "button");
		cardBtnWatch.setAttribute("data-url", movie.torrentLink);
		cardBtnWatch.textContent = "Смотреть";
		cardBtnWatch.addEventListener("click", watchBtn);

		cardBtnHolder.appendChild(cardBtnWatch);

		const cardBtnFavor = document.createElement("button");
		cardBtnFavor.id = movie.id;
		cardBtnFavor.setAttribute("type", "button");
		cardBtnFavor.setAttribute("data-title", movie.enTitle);
		if (storeArray.includes(movie.enTitle)) {
			cardBtnFavor.className = "btn-card fav-on";
			cardBtnFavor.textContent = "Удалить";
			cardBtnFavor.addEventListener("click", removeFromFav);
		} else {
			cardBtnFavor.className = "btn-card";
			cardBtnFavor.textContent = "В Избранное";
			cardBtnFavor.addEventListener("click", addToFav);
		}
		cardBtnHolder.appendChild(cardBtnFavor);
		cardDescription.appendChild(cardBtnHolder);
		card.appendChild(cardDescription);
		//output.appendChild(card);
		list.appendChild(card);

		card.addEventListener("click", (e) => {
			e.stopPropagation();
			window.location.assign(`./info.html?id=${movie.id}`);
		});
	});

	document.getElementById("foot").style.display = "none";
}

function watchBtn(e) {
	e.stopPropagation();
	let url = e.target.dataset.url;
	document.location = url;
}

function fetchData(line) {
	fetch(`/fav`, {
		method: "POST",
		body: line,
	})
		.then((res) => res.json())
		.then((data) => render(data));
}

function goToMoviePage(e) {
	document.location = "./info.html?id=" + e.id;
}

function searchReq(e) {
	e.preventDefault();
	let input = document.getElementById("search-input").value;
	window.location.assign(`search.html?title=${input}`);
}

function addToFav(e) {
	e.stopPropagation();
	const item = e.target.dataset.title;
	let storeArray = JSON.parse(localStorage.getItem(STORAGE_FAV));

	if (!storeArray) {
		storeArray = [];
	}
	if (storeArray.includes(item)) {
		return;
	}
	storeArray.push(item);
	localStorage.setItem(STORAGE_FAV, JSON.stringify(storeArray));

	toggleFavBtn(e.target, true);
}

function removeFromFav(e) {
	e.stopPropagation();
	const item = e.target.dataset.title;

	let storeArray = JSON.parse(localStorage.getItem(STORAGE_FAV));
	storeArray.splice(storeArray.indexOf(item), 1);
	localStorage.setItem(STORAGE_FAV, JSON.stringify(storeArray));

	toggleFavBtn(e.target, false);
}

function toggleFavBtn(element, isAdded) {
	if (isAdded) {
		element.className = "btn-card fav-on";
		element.innerHTML = "Удалить";
		element.removeEventListener("click", addToFav);
		element.addEventListener("click", removeFromFav);
	} else {
		element.className = "btn-card";
		element.innerHTML = "В Избранное";
		element.removeEventListener("click", removeFromFav);
		element.addEventListener("click", addToFav);
	}

	renderCounter();
}
