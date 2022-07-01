let offset;
let category;
let length = 10;
const STORAGE_FAV = "webtv-fav";
let movies;

const footter = document.getElementById("page-id");
const prvBtn = document.getElementById("back");
const nextBtn = document.getElementById("forw");
document.getElementById("search-button").addEventListener("click", searchReq);
document.getElementById("form-id").addEventListener("submit", searchReq);
prvBtn.addEventListener("click", prevPage);
nextBtn.addEventListener("click", nextPage);

let param = document.location.href.split("?")[1];
if (param == undefined) {
	offset = 0;
	category = "top";
} else {
	category = param.split("&")[0].split("=")[1];
	offset = parseInt(param.split("&")[1].split("=")[1]);
	length = parseInt(param.split("&")[2].split("=")[1]);
}

const renderCounter = () => {
	let fcounter = JSON.parse(localStorage.getItem("webtv-fav")).length;
	document.getElementById("fav-counter").innerHTML = `<sub>${fcounter}</sub>`;
};

fetchData(offset, length);
renderCounter();

function fetchData(offset, length) {
	fetch(`list?offset=${offset}&length=${length}`)
		.then((res) => res.json())
		.then((data) => render(data))
		.then(() => fetchSize());

	offset += length;
}

function render(data) {
	movies = data;

	const storeArray = localStorage.getItem(STORAGE_FAV) ?? [];
	const list = document.getElementById("list");

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

		const cardPs = {
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
		}
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
		list.appendChild(card);

		card.addEventListener("click", (e) => {
			e.stopPropagation();
			window.location.assign(`./info.html?id=${movie.id}`);
		});
	});

	//fetchSize();
}

function nextPage() {
	offset += length;
	window.location.assign(`?cat=${category}&offset=${offset}&length=${length}`);
}

function prevPage() {
	offset -= length;
	window.location.assign(`?cat=${category}&offset=${offset}&length=${length}`);
}

function watchBtn(e) {
	e.stopPropagation();
	let url = e.target.dataset.url;
	document.location = url;
}

function fetchSize() {
	fetch("list_size?category=top")
		.then((res) => res.text())
		.then((data) => renderPageNav(data));
}

function goToMoviePage(e) {
	document.location = "./info.html?id=" + e.id;
}

function renderPageNav(size) {
	let ul = document.createElement("ul");
	ul.id = "page-ul";
	ul.className = "page-ul-nav";

	if (offset > length * 2) {
		let li = document.createElement("li");
		li.className = "page-li";
		li.id = 0;
		li.innerHTML = "...";
		ul.appendChild(li);
	}

	for (
		let index = Math.max(offset - length * 2, 0), pag = index / length + 1;
		index < Math.min(size, length * 3 + offset);
		index += length, pag++
	) {
		max = pag;
		let li = document.createElement("li");
		li.id = index;
		li.className = "page-li";
		if (offset == index) {
			li.className = "page-li active";
		}
		li.innerHTML = pag;
		ul.appendChild(li);
	}

	if (offset < size - length * 3) {
		let li = document.createElement("li");
		li.className = "page-li";
		li.id = Math.trunc(size / length) * length;
		li.innerHTML = "...";
		ul.appendChild(li);
	}

	footter.appendChild(ul);
	document.getElementById("page-ul").addEventListener("click", pageGo);

	checkActivePage();
	document.getElementById("foot").style.display = "flex";
}

function pageGo(e) {
	offset = e.target.id;
	window.location.assign(`?cat=${category}&offset=${offset}&length=${length}`);
}

function searchReq(e) {
	e.preventDefault();
	let input = document.getElementById("search-input").value;
	window.location.assign(`search.html?title=${input}`);
}

function checkActivePage() {
	let active = document.getElementsByClassName("active")?.[0].id;
	let last = document.getElementById("page-ul").lastChild.id;
	if (active == 0) {
		prvBtn.disabled = true;
		prvBtn.style.display = "none";
	}
	if (active == last) {
		nextBtn.disabled = true;
		nextBtn.style.display = "none";
	}
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
