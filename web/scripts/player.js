// JavaScript Document
var player;
var query; //search query
var query2; //delayed query

var currentSong;
var currentIndex;
var currentPlaylist = new Array();
var shuffle = false; //shuffle enabled or not 
var shuffleIndexes = new Array();

//Initialize views, collections and current
var views = new Object();
var collections = new Object();
var currentView;
var currentCollection;
var currentData; //copy of current data in use

/*
 * Initial Operations
 */
$(document).ready(function(){
	
	//hide middle
	$("#middle").hide();
	
	//Setup View object
	var View = function View(data) {
		this.value = data;
	}
	//Define function for changing view
	View.prototype.change = function(e){
		//Only if different from current view
		if(currentView != this) {
			//delete current viewModel data and set new current view
			currentView.viewModel.list.removeAll();
			currentView = this;
			//load new view
			$.getJSON('/resources/json/'+ currentCollection.value + currentView.value +'.json', function(data) {
		   		currentData = data.list;
		   		if(query() == "" || query() == null) {
					ko.mapping.fromJS(data, currentView.viewModel);
					setCurrentPlayList();
				}
		   		else searching(query());
	  	 	});
	  	 	//Change buttons
	  	 	$(e.target.parentNode).children().removeClass("btnSelected");
	  	 	$(e.target).addClass("btnSelected");
		}
	};
	
	//Setup Collection object
	var Collection = function Collection(data) {
		this.value = data;
	}
	//Define function for changing Collection
	Collection.prototype.change = function(e){
		//Only if different from current view
		if(currentCollection != this) {
			//change current Collection
			currentCollection = this;
			//load new view
			$.getJSON('/resources/json/'+ currentCollection.value + currentView.value +'.json', function(data) {
		   		currentData = data.list;
		   		if(query() == "" || query() == null) {
					ko.mapping.fromJS(data, currentView.viewModel);
					setCurrentPlayList();
				}
		   		else searching(query());
	  	 	});
	  	 	//Change buttons
	  	 	$(e.target.parentNode).children().removeClass("btnSelected");
	  	 	$(e.target).addClass("btnSelected");
		}
	};
	
	//add all views
	views.Artists = new View("Artists");
	views.Albums = new View("Albums");
	views.Songs = new View("Songs"); 
	
	//add all collections
	collections.All = new Collection("All");
	collections.Comp = new Collection("Comp");
	
	//Set initial current view and collection
	currentView = views.Artists; //either 'Song' 'Artists' or 'Albums'
	currentCollection = collections.All; //either 'All' or 'Comp'
	
	//initialize viewmodels
	views.Artists.viewModel = ko.mapping.fromJS({list:[]});
	views.Albums.viewModel = ko.mapping.fromJS({list:[]});
	views.Songs.viewModel = ko.mapping.fromJS({list:[]});
	//connect views with viewmodels
	ko.applyBindings(views.Artists.viewModel, $("#byartist")[0]);
	ko.applyBindings(views.Albums.viewModel, $("#byalbum")[0]);
	ko.applyBindings(views.Songs.viewModel, $("#bysong")[0]);
	
	//initialize player
	player = new MediaElementPlayer('#audioPlayer',{success: function (mediaElement, domObject) { 
        mediaElement.addEventListener('ended', playNext, false);
    }});
	
	//load data
   	$.getJSON('/resources/json/'+ currentCollection.value + currentView.value +'.json', function(data) {
	   currentData = data.list;
	   ko.mapping.fromJS(data, views.Artists.viewModel);
	   $("#middle").show();
  	 }).done(function(){ //set current playlist
		   setCurrentPlayList();
	   });
   
   	//set selected buttons
   	$($("#viewNav")[0].firstElementChild).addClass("btnSelected");
    $($("#collectionNav")[0].firstElementChild).addClass("btnSelected");
   
  	//set search function
  	query = ko.observable('');
   	query2 = ko.computed(function() {
    	return query();
		}).extend({ throttle: 400 });
   	query2.subscribe(searching);
   	ko.applyBindings(query, $("#right")[0]);
   
 });

/*
 * Play Function
 */
function playSong(root, data) {
	player.pause();
	player.setSrc(data.requestPath());
	player.load();
	player.play();
	//set currently song
	if(currentView != views.Songs) { 
		$("#currentImg")[0].src = root.cover_filepath();
		$("#currentImg").show();
	}
	else {
		$("#currentImg").hide();
	}
	$("#currentArtist").html(data.artist());
	$("#currentSong").html(data.title());
	$("#current").fadeIn(500);
	currentSong = {requestPath:data.requestPath()};
	if(shuffle) {
		shuffleIndexes.length = 0;
		var temp = new Array();
		for(var x = 0;x<currentPlaylist.length;x++) {
			temp.push(x);
		}
		for(var x = 0;x<currentPlaylist.length;x++) {
			var r = temp.splice(Math.floor(Math.random()*temp.length),1);
			shuffleIndexes.push(r[0]);
		}
	}
	setNextSong();
 };
 
 function setNextSong() {
	$("#next").hide();
	for(var x in currentPlaylist) {
		if(currentPlaylist[x].requestPath == currentSong.requestPath){
			currentIndex = parseInt(x);
			break;
		}
	}
	if(shuffle) {
		for(var x in shuffleIndexes) {
			if(shuffleIndexes[x] == currentIndex) {
				currentIndex = parseInt(x);
				break;
			}
		}
		$("#nextArtist").html(currentPlaylist[(shuffleIndexes[currentIndex+1])%currentPlaylist.length].artist);
		$("#nextSong").html(currentPlaylist[(shuffleIndexes[currentIndex+1])%currentPlaylist.length].title);
		setTimeout('$("#next").fadeIn(1000)',1500);
	}
	else if(currentIndex < currentPlaylist.length-1) {
		$("#nextArtist").html(currentPlaylist[currentIndex+1].artist);
		$("#nextSong").html(currentPlaylist[currentIndex+1].title);
		setTimeout('$("#next").fadeIn(1000)',1500);
	}
 }
 
function playNext(){
	if(shuffle) {
		player.pause();
		player.setSrc(currentPlaylist[(shuffleIndexes[currentIndex+1])%currentPlaylist.length].requestPath);
		player.load();
		player.play();
		var song = currentPlaylist[(shuffleIndexes[currentIndex+1])%currentPlaylist.length];
		if(currentView != views.Songs) { 
			$("#currentImg")[0].src = song.cover_filepath;
			$("#currentImg").show();
		}
		else {
			$("#currentImg").hide();
		}
		$(".currentTitle").show();
		$("#currentArtist").html(song.artist);
		$("#currentSong").html(song.title);
		currentSong = {requestPath:song.requestPath};
		setNextSong();
	}
	else if(currentIndex < currentPlaylist.length-1) {
		player.pause();
		player.setSrc(currentPlaylist[currentIndex+1].requestPath);
		player.load();
		player.play();
		var song = currentPlaylist[currentIndex+1];
		if(currentView != views.Songs) { 
			$("#currentImg")[0].src = song.cover_filepath;
			$("#currentImg").show();
		}
		else {
			$("#currentImg").hide();
		}
		$(".currentTitle").show();
		$("#currentArtist").html(song.artist);
		$("#currentSong").html(song.title);
		currentSong = {requestPath:song.requestPath};
		setNextSong();
	}
 }
 
 function playPrevious(){
	 if(shuffle) {
		player.pause();
		var bla = (shuffleIndexes[currentIndex-1])%currentPlaylist.length;
		player.setSrc(currentPlaylist[(shuffleIndexes[currentIndex-1])%currentPlaylist.length].requestPath);
		player.load();
		player.play();
		var song = currentPlaylist[(shuffleIndexes[currentIndex-1])%currentPlaylist.length];
		if(currentView != views.Songs) { 
			$("#currentImg")[0].src = song.cover_filepath;
			$("#currentImg").show();
		}
		else {
			$("#currentImg").hide();
		}
		$(".currentTitle").show();
		$("#currentArtist").html(song.artist);
		$("#currentSong").html(song.title);
		currentSong = {requestPath:song.requestPath};
		setNextSong();
	}
	else if(currentIndex != 0) {
		player.pause();
		player.setSrc(currentPlaylist[currentIndex-1].requestPath);
		player.load();
		player.play();
		var song = currentPlaylist[currentIndex-1];
		if(currentView != views.Songs) { 
			$("#currentImg")[0].src = song.cover_filepath;
			$("#currentImg").show();
		}
		else {
			$("#currentImg").hide();
		}
		$(".currentTitle").show();
		$("#currentArtist").html(song.artist);
		$("#currentSong").html(song.title);
		currentSong = {requestPath:song.requestPath};
		setNextSong();
	}
 }
 
 function setCurrentPlayList() {
	currentPlaylist.length = 0;
 	if(currentView == views.Artists) {
		//change currentPlaylist
		for(var x in currentView.viewModel.list()) {
			for(var y in currentView.viewModel.list()[x].albums()) {
				for( var z in currentView.viewModel.list()[x].albums()[y].songs()) {
					var s = currentView.viewModel.list()[x].albums()[y].songs()[z];
					currentPlaylist.push({artist:s.artist(), title:s.title(), requestPath:s.requestPath(),
										cover_filepath:currentView.viewModel.list()[x].albums()[y].cover_filepath()});
				}
			}
		}
	}
	else if(currentView == views.Albums) {
		//change currentPlaylist
		for(var x in currentView.viewModel.list()) {
			for( var y in currentView.viewModel.list()[x].songs()) {
				var s = currentView.viewModel.list()[x].songs()[y];
				currentPlaylist.push({artist:s.artist(), title:s.title(), requestPath:s.requestPath(),
									cover_filepath:currentView.viewModel.list()[x].cover_filepath()});
			}
				
		}
	}
	else if(currentView == views.Songs) {
		//change currentPlaylist
		for(var x in currentView.viewModel.list()) {
				var s = currentView.viewModel.list()[x];
				currentPlaylist.push({artist:s.artist(), title:s.title(), requestPath:s.requestPath()});
		}
	}
 }
 
 function toggleShuffle() {
 	if(shuffle) {
		$("#shuffle").html("shuffle off");
		$("#shuffle").css("color", "#999");
		shuffle = false;
		setNextSong();
	}
	else {
		$("#shuffle").html("shuffle on");
		$("#shuffle").css("color", "#FFF");
		shuffle = true;
		if(shuffleIndexes.length == 0) {
			shuffleIndexes.length = 0;
			var temp = new Array();
			for(var x = 0;x<currentPlaylist.length;x++) {
				temp.push(x);
			}
			for(var x = 0;x<currentPlaylist.length;x++) {
				var r = temp.splice(Math.floor(Math.random()*temp.length),1);
				shuffleIndexes.push(r[0]);
			}
		}
		setNextSong();
	}
 }
 
 
  /*
   * Search Function
   */
   var searching = function(value) {
     //remove data in viewmodel
   	 currentView.viewModel.list.removeAll();
	 //create result array
	 var results = new Array();
	 
	 if(currentView == views.Artists) {
	 	//set artists array
	 	var artists = currentData;
		//search for matching artists, albums, songs
		for(var x in artists) {
			if(artists[x].name.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
				 results.push(artists[x]);
			}
			else {
				var count = 0;
				var artist = {name:artists[x].name,albums:new Array()};
				for(var y in artists[x].albums)
				{
					if(artists[x].albums[y].title.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
						count++;
						artist.albums.push(artists[x].albums[y]);
					}
					else {
						var count1 = 0;
						var album = {title:artists[x].albums[y].title, ID:artists[x].albums[y].ID, cover_filepath:artists[x].albums[y].cover_filepath, songs: new Array()};
						for(var z in artists[x].albums[y].songs) {
							if(artists[x].albums[y].songs[z].title.toLowerCase().indexOf(value.toLowerCase()) >= 0||
								artists[x].albums[y].songs[z].artist.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
								count1++;
								album.songs.push(artists[x].albums[y].songs[z]);
							}
						}
						if(count1 > 0) {
							count++;
							artist.albums.push(album);
						}
					}
				}
				if(count > 0) results.push(artist);	
			}
		}
	 }
	 else if (currentView == views.Albums) {
	 	//set albums array
	 	var albums = currentData;
		//search for matching albums, songs
		for(var x in albums) {
			if(albums[x].title.toLowerCase().indexOf(value.toLowerCase()) >= 0 || 
				albums[x].albumArtist.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
				 results.push(albums[x]);
			}
			else {
				var count = 0;
				var album = {title:albums[x].title, albumArtist:albums[x].albumArtist, ID:albums[x].ID, cover_filepath:albums[x].cover_filepath, songs: new Array()};
				for(var y in albums[x].songs)
				{
					if(albums[x].songs[y].title.toLowerCase().indexOf(value.toLowerCase()) >= 0||
						albums[x].songs[y].artist.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
						count++;
						album.songs.push(albums[x].songs[y]);
					}
				}
				if(count > 0) results.push(album);	
			}
		}
	 }
	 else if (currentView == views.Songs) {
	 	//set songs array
	 	var songs = currentData;
		//search for matching albums
		for(var x in songs) {
			if(songs[x].title.toLowerCase().indexOf(value.toLowerCase()) >= 0 || 
				songs[x].album.toLowerCase().indexOf(value.toLowerCase()) >= 0 ||
				songs[x].artist.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
				 results.push(songs[x]);
			}
		}
	 }
	 ko.mapping.fromJS({list:results}, currentView.viewModel);
	 setCurrentPlayList();
  };