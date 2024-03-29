// JavaScript Document
var player;
var query; //search query
var query2; //delayed query
var viewModel_artist;
var viewModel_album;
var viewModel_song;
var currentView; //either 'all' , 'comp' or 'playlist'
var artists;
var albums;
var songs;
var allMusic_views = new Array(); // Array containing the 3 views (artist,album,song) for all music
var comp_views = new Array(); //Array containing the 3 views (artist,album,song) for compilations
var artistSort = true;
var albumSort = false;
var songSort = false;
var currentSong;
var currentIndex;
var currentPlaylist = new Array();
var shuffle = false; //shuffle enabled or not 
var shuffleIndexes = new Array();

$(document).ready(function(){
	
	//hide middle
	$("#middle").hide();
	
	player = new MediaElementPlayer('#audioPlayer',{success: function (mediaElement, domObject) { 
        mediaElement.addEventListener('ended', playNext, false);
    }});
		
   $.getJSON('/CollectionService?action=allartists', function(data) {
	   allMusic_views[0] = data.list;
	   artists = data.list;
	   viewModel_artist = ko.mapping.fromJS(data);
	   ko.applyBindings(viewModel_artist, $("#byartist")[0]);
	   $("#middle").show();
   }).done(function(){ //set current playlist
		   for(var x in viewModel_artist.list()) {
				for(var y in viewModel_artist.list()[x].albums()) {
					for( var z in viewModel_artist.list()[x].albums()[y].songs()) {
						var s = viewModel_artist.list()[x].albums()[y].songs()[z];
						currentPlaylist.push({artist:s.artist(), title:s.title(), requestPath:s.requestPath(),
											cover_filepath:viewModel_artist.list()[x].albums()[y].cover_filepath()});
					}
				}
		   }
	   });
   
   //set Current view
   currentView = "all";
   
   //set selected buttons
   $("#artistSortBtn").addClass("btnSelected");
   $("#allmusicViewBtn").addClass("btnSelected");
   
   //set search function
   query = ko.observable('');
   query2 = ko.computed(function() {
    return query();
	}).extend({ throttle: 400 });
   query2.subscribe(searching);
   ko.applyBindings(query, $("#right")[0]);
   
   //set Album viewmodel
   $.getJSON('/CollectionService?action=allalbums', function(data) {
	   allMusic_views[1] = data.list;
	   albums = data.list;
	   viewModel_album = ko.mapping.fromJS({list:[]});
	   ko.applyBindings(viewModel_album, $("#byalbum")[0]);
    });
   //set Song viewmodel
   $.getJSON('/CollectionService?action=allsongs', function(data) {
	   allMusic_views[2] = data.list;
	   songs = data.list;
	   viewModel_song = ko.mapping.fromJS({list:[]});
	   ko.applyBindings(viewModel_song, $("#bysong")[0]);
    });
   
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
	if(songSort == false) { 
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
		if(songSort == false) { 
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
		if(songSort == false) { 
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
		if(songSort == false) { 
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
		if(songSort == false) { 
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
 	if(artistSort) {
		//change currentPlaylist
		for(var x in viewModel_artist.list()) {
			for(var y in viewModel_artist.list()[x].albums()) {
				for( var z in viewModel_artist.list()[x].albums()[y].songs()) {
					var s = viewModel_artist.list()[x].albums()[y].songs()[z];
					currentPlaylist.push({artist:s.artist(), title:s.title(), requestPath:s.requestPath(),
										cover_filepath:viewModel_artist.list()[x].albums()[y].cover_filepath()});
				}
			}
		}
	}
	else if(albumSort) {
		//change currentPlaylist
		for(var x in viewModel_album.list()) {
			for( var y in viewModel_album.list()[x].songs()) {
				var s = viewModel_album.list()[x].songs()[y];
				currentPlaylist.push({artist:s.artist(), title:s.title(), requestPath:s.requestPath(),
									cover_filepath:viewModel_album.list()[x].cover_filepath()});
			}
				
		}
	}
	else if(songSort) {
		//change currentPlaylist
		for(var x in viewModel_song.list()) {
				var s = viewModel_song.list()[x];
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
 * Collection functions
 */
function changeCollection(coll) {
  	if(coll == "all" && currentView != "all") {
		currentView = "all";
		artists = allMusic_views[0];
		albums = allMusic_views[1];
		songs = allMusic_views[2];
		if(artistSort) {
			artistSort = false;
			sortByArtist();
		}
		else if(albumSort) {
			albumSort = false;
			sortByAlbum();
		}
		else if(songSort) {
			songSort = false;
			sortBySong();
		}
		$("#allmusicViewBtn").addClass("btnSelected");
		$("#compViewBtn").removeClass("btnSelected");
	}
	else if (coll == "comp" && currentView != "comp") {
		currentView = "comp";
		if(comp_views.length == 0) {
			$.getJSON('/CollectionService?action=compartists', function(data) {
		    	comp_views[0] = data.list;
				artists = data.list;
				if(artistSort) {
					artistSort = false;
					sortByArtist();
				}
		    }); 
			$.getJSON('/CollectionService?action=compalbums', function(data) {
		    	comp_views[1] = data.list;
				albums = data.list;
				if(albumSort) {
					albumSort = false;
					sortByAlbum();
				}
		    });  
			$.getJSON('/CollectionService?action=compsongs', function(data) {
		    	comp_views[2] = data.list;
				songs = data.list;
				if(songSort) {
					songSort = false;
					sortBySong();
				}
		    });   
		}
		else {
			artists = comp_views[0];
			albums = comp_views[1];
			songs = comp_views[2];
			if(artistSort) {
				artistSort = false;
				sortByArtist();
			}
			else if(albumSort) {
				albumSort = false;
				sortByAlbum();
			}
			else if(songSort) {
				songSort = false;
				sortBySong();
			}
		}
		$("#allmusicViewBtn").removeClass("btnSelected");
		$("#compViewBtn").addClass("btnSelected");
	}
}

/*
* Sort Functions
*/
function sortByArtist() {
  	if(!artistSort) {
		$("#artistLoad").show();
		artistSort = true;
		albumSort = false;
		songSort = false;
		viewModel_album.list.removeAll();
		viewModel_song.list.removeAll();
		if(query() == "" || query() == null) { 
			ko.mapping.fromJS({list:artists}, viewModel_artist);
			setCurrentPlayList();
		}
		else searching(query());
		//change selected buttons
		$("#artistSortBtn").addClass("btnSelected");
		$("#albumSortBtn").removeClass("btnSelected");
		$("#songSortBtn").removeClass("btnSelected");
		$("#artistLoad").hide();
	}
  };
  
  function sortByAlbum() {
  	if(!albumSort) {
		$("#albumLoad").show();
		albumSort = true;
		artistSort = false;
		songSort = false;
		viewModel_artist.list.removeAll();
		viewModel_song.list.removeAll();
		if(query() == "" || query() == null) {
			ko.mapping.fromJS({list:albums}, viewModel_album);
			setCurrentPlayList();
		}
	    else searching(query());
		//change selected buttons
		$("#artistSortBtn").removeClass("btnSelected");
		$("#albumSortBtn").addClass("btnSelected");
		$("#songSortBtn").removeClass("btnSelected");
		$("#albumLoad").hide();
	}
  };
  
  function sortBySong() {
  	if(!songSort) {
		$("#songLoad").show();
		albumSort = false;
		artistSort = false;
		songSort = true;
		viewModel_artist.list.removeAll();
		viewModel_album.list.removeAll();
		if(query() == "" || query() == null) {
	    	ko.mapping.fromJS({list:songs}, viewModel_song);
			setCurrentPlayList();
		}
		else searching(query());
		//change selected buttons
		$("#artistSortBtn").removeClass("btnSelected");
		$("#albumSortBtn").removeClass("btnSelected");
		$("#songSortBtn").addClass("btnSelected");
		$("#songLoad").hide();
	}
  };
  
  /*
   * Search Function
   */
   var searching = function(value) {
	 if(artistSort) {
  	 // remove all the current artists
		viewModel_artist.list.removeAll();
		var results = new Array();
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
		ko.mapping.fromJS({list:results}, viewModel_artist);
	 }
	 else if (albumSort) {
  	 	// remove all the current albums
		viewModel_album.list.removeAll();
		var results = new Array();
		//search for matching albums
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
		ko.mapping.fromJS({list:results}, viewModel_album);
	 }
	 else if (songSort) {
	 	// remove all the current songs
		viewModel_song.list.removeAll();
		var results = new Array();
		//search for matching albums
		for(var x in songs) {
			if(songs[x].title.toLowerCase().indexOf(value.toLowerCase()) >= 0 || 
				songs[x].album.toLowerCase().indexOf(value.toLowerCase()) >= 0 ||
				songs[x].artist.toLowerCase().indexOf(value.toLowerCase()) >= 0) {
				 results.push(songs[x]);
			}
		}
		ko.mapping.fromJS({list:results}, viewModel_song);
	 }
	 setCurrentPlayList();
  };