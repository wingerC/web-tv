const STORAGE_FAV = "webtv-fav";

document.getElementById("search-button").addEventListener("click", searchReq);
document.getElementById("form-id").addEventListener("submit", searchReq);

let param = document.location.href.split("?")[1];
if (param == undefined) {
} else {
	id = param.split("=")[1];
	fetchMovie(id);
}

const renderCounter = () => {
	let fcounter = JSON.parse(localStorage.getItem(STORAGE_FAV)).length;
	document.getElementById("fav-counter").innerHTML = `<sub>${fcounter}</sub>`;
};

renderCounter();

function fetchMovie(id) {
	fetch(`movie?id=${id}`)
		.then((res) => res.json())
		.then((data) => render(data))
		.then(movie=> fetchAltLinks(movie));
}

function render(movie) {
	if (!movie) {
		document.getElementById("info-list").innerHTML = "";
		return;
	}

	const storeArray = localStorage.getItem(STORAGE_FAV) ?? [];

	const list = document.getElementById("info-list");
	const output = document.createElement("div");
	output.className = "clearfix";
	output.appendChild(document.createElement("br"));

	let srcPost = movie.description.src;

	const poster = document.createElement("img");
	poster.className = "info-poster";
	poster.setAttribute("src", srcPost);
	poster.setAttribute("width", 400);
	poster.setAttribute("height", 470);
	output.appendChild(poster);

	for (const [key, value] of Object.entries(movie.description)) {
		if (key == "id" || key == "undef" || key == "src") continue;
		let p = document.createElement("p");
		p.className = `info-${key}`;
		p.innerHTML = value;
		output.appendChild(p);

		if (key == "title") {
			let pFullTitle = document.createElement("p");
			pFullTitle.className = `info-full-title`;
			pFullTitle.innerHTML = movie.title;
			output.appendChild(pFullTitle);

			let pYear = document.createElement("p");
			pYear.className = `info-year`;
			pYear.innerHTML = movie.year;
			output.appendChild(pYear);
		}
		
	}

	let pSize = document.createElement("p");
	pSize.className = `info-size`;
	pSize.innerHTML = movie.size;
	output.appendChild(pSize);

	

	let pPeers = document.createElement("p");
	pPeers.className = `info-peers`;
	pPeers.innerHTML = movie.peers;
	output.appendChild(pPeers);

	const cardBtnHolder = document.createElement("div");
	cardBtnHolder.className = "info-btn-holder";

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
	output.appendChild(cardBtnHolder);

	list.appendChild(output);
	
	return movie;
}

function watchBtn(e) {
	document.location = e.target.dataset.url;
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

async function fetchAltLinks(mov){
	const req = await fetch(`alt?title=${mov.enTitle}`);
	const movies = await req.json();

	if (movies.length > 1){
	renderAltLinks(movies, mov.id);
	document.querySelector('.alt').style.display = 'block';
	}

	
}

function renderAltLinks(movies, id){
	const ul = document.querySelector('.alt-links');
	
	const makeLi = (movie) => {
		const li =  document.createElement('li');
		const a = document.createElement('a');
		a.setAttribute('href', `./info.html?id=${movie.id}`);
		a.textContent = `${movie.size} / ${movie.peers} - ${movie.title}`;
		li.appendChild(a);
		return li;
	};

	movies.forEach(m => {
		if (m.id != id) ul.appendChild(makeLi(m))
	});

}

