var player;
var query; //search query
var query2; //delayed search query
var viewModel_artist;
var viewModel_album;
var viewModel_song;
var viewModel_editAlbum;
var currentView; //either 'all' , 'identified' , 'unidentified' or 'nocover'
var artists;
var albums;
var songs;
var allMusic_views = new Array(); // Array containing the 3 views (artist,album,song) for all music
var identified_views = new Array(); // Array containing the 3 views (artist,album,song) for identified music
var unidentified_views = new Array(); // Array containing the 3 views (artist,album,song) for unidentified music
var nocover_views = new Array(); // Array containing the 3 views (artist,album,song) for albums missing a cover
var artistSort = true;
var albumSort = false;
var songSort = false;
var currentSong;
var viewModel_folders;
var editCoverAlbumID; //ID of album when editing cover


// JavaScript Document
 $(document).ready(function(){
	
	$("#middle").hide();//hide middle
	
	player = new MediaElementPlayer('#audioPlayer');
		
   $.getJSON('/resources/json/AllArtists.json', function(data) {
	   allMusic_views[0] = data.list;
	   artists = data.list;
	   viewModel_artist = ko.mapping.fromJS(data);
	   ko.applyBindings(viewModel_artist, $("#byartist")[0]);
	   bindOverlays(); //bind edit button overlays
	    $("#middle").show();//show middle div
   });
   
   //set Current view
   currentView = "all";
   
   //set selected buttons
   $("#artistSortBtn").addClass("btnSelected");
   $("#allmusicViewBtn").addClass("btnSelected");
   
   //set Album viewmodel
   $.getJSON('/resources/json/AllAlbums.json', function(data) {
	   allMusic_views[1] = data.list;
	   albums = data.list;
	   viewModel_album = ko.mapping.fromJS({list:[]});
	   ko.applyBindings(viewModel_album, $("#byalbum")[0]);
    });
   //set Song viewmodel
   $.getJSON('/resources/json/AllSongs.json', function(data) {
	   allMusic_views[2] = data.list;
	   songs = data.list;
	   viewModel_song = ko.mapping.fromJS({list:[]});
	   ko.applyBindings(viewModel_song, $("#bysong")[0]);
    });
	
	//set search function
   query = ko.observable('');
   query2 = ko.computed(function() {
    return query();
	}).extend({ throttle: 400 });
   query2.subscribe(searching);
   ko.applyBindings(query, $("#right")[0]);
	
	//create folders overlay
	$("h4[rel]").overlay({mask: {
        color: '#fff',
        loadSpeed: 200,
        opacity: 0.5,
		onLoad: openOverlay()
    }});
	
	//get Configuration
 	$.getJSON('/ManagementService?action=getConfiguration', function(data) {
	   viewModel_folders = ko.mapping.fromJS(data);
	   ko.applyBindings(viewModel_folders, $("#conf")[0]);
   });  
   
   //create empty editalbum viewmodel
   viewModel_editAlbum = ko.mapping.fromJS({title:"",ID:"",albumArtist:"",songs:[],cover_filepath:"", compilation:false, year:"",
   no_oftracks: "", disc_no: ""});
   ko.applyBindings(viewModel_editAlbum, $("#editalbum")[0]);
 });
 
 /*
  * Editing functions
  */
 function bindOverlays() {
 	//apply overlays
	   $("button[rel]").overlay({mask: {
        color: '#fff',
        loadSpeed: 200,
        opacity: 0.5,
   	   }, fixed:false});
 }
 //open the overlay and show the album values
 function editingAlbum(data) {
 	$.getJSON('/ManagementService?action=getAlbum&albumid=' + data.ID(), function(data) {
	   ko.mapping.fromJS(data ,viewModel_editAlbum);
   	});
 }
 //update album 
 function editAlbum(data) {
	$(".loading").show();
 	var album = ko.mapping.toJS(data);
	$.ajax({
			type: 'POST',
			url: "/ManagementService?action=editAlbum",
			data: {data: JSON.stringify(album)}
	}).done(function( msg ) {
  			updateView();
			$(".loading").hide();
		});
 }
 
 function editingCover(root, data) {
	 var url = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCNqU4PPechS6kPNytAA1JGI4gJspXnWK4&cx=007383358878722163893:qavasfbitxe&searchType=image&q=";
	 var query = data.title() + " " + root.name();
	 editCoverAlbumID = data.ID();
	 $.getJSON(url + query, function(data) {
	     var imgHtml = "<h3>Edit Cover</h3><hr><table><tr>";
		 for(var x = 0;x<5;x++) {
			 if(data.items.length > x) {
				imgHtml += "<td><img src='" + data.items[x].image.thumbnailLink +
				"'><br><input type='radio' name='covers' value='" + data.items[x].image.thumbnailLink + "'>select</td>"; 
			}
		 }
		 imgHtml += "<tr></tr>";
		 for(var x = 5;x<10;x++) {
			 if(data.items.length > x) {
				 imgHtml += "<td><img src='" + data.items[x].image.thumbnailLink +
				"'><br><input type='radio' name='covers' value='" + data.items[x].image.thumbnailLink + "'>select</td>"; 
			}
		 }
		 imgHtml += "</tr></table><button onClick=editCover(" + editCoverAlbumID + ")>save cover</button><span class=loading>saving...<img src='resources/loading/ajax-loader.gif'></span>";
		 $("#editcoverJS")[0].innerHTML = imgHtml;
   	});
 }
 
 function editCover(id) {
	$(".loading").show();
 	var imgLink = $('input[name=covers]:checked').val();
	$.ajax({
			type: 'POST',
			url: "/ManagementService?action=editCover",
			data: {url: imgLink, ID: id}
	}).done(function( msg ) {
  			updateView();
			$(".loading").hide();
		});
 }
 
 function updateView() {
 	allMusic_views.length = 0;
	identified_views.length = 0;
	unidentified_views.length = 0;
	nocover_views.length = 0;
	var view = currentView;
	currentView = "";
	if(view == "all") changeCollection(view);
	else if (view == "identified") changeCollection(view);
	else if (view == "unidentified") changeCollection(view);
	else if (view == "nocover") changeCollection(view);
 }
 /*
  * Configuration Functions
  */

 function openOverlay() {
	 //create tree
 	$.getJSON('/ManagementService?action=getFolders', function(data) {
	    $("#folderTree").dynatree({children:data, 
		onLazyRead: function(node){
        	$.getJSON('/ManagementService?action=getFolders&folder='+node.data.key, function(data) {
				node.addChild(data);
				})
    		}
		});
   	})
 };
 
 function addFolder() {
	 var node = $("#folderTree").dynatree("getActiveNode");
	 if(node != null) {
		 var data = {action:"addFolder", folder: ""+node.data.key};
		 $.ajax({
			type: 'POST',
			url: "/ManagementService",
			data: data,
			dataType: "json"
		});
		$("#folderTree").dynatree("getTree").reload();
	 	viewModel_folders.folders.push({folder: ""+node.data.key});
	 };
 };
 
 function removeFolders() {
	 $("#removeLoad").show();
	 var fl = $("#folderForm input[type='radio']:checked").val();
	 if(fl != null) {
	 	$.ajax({
			type:'POST',
			url: "/ManagementService",
			data: {action:"removeFolder", folder: fl}
		}).done(function( msg ) {
  			viewModel_folders.folders.remove(function(item) { return item.folder == fl });
			updateView();
			$("#removeLoad").hide();
		});
	 }
  };
  
  function indexFolders() {
	  $("#indexLoad").show();
  	  $.ajax({
	  	type:'POST',
	  	url: "/ManagementService",
		data: {action:"index"}
	  }).done(function( msg ) {
  			updateView();
			$("#indexLoad").hide();
		});
  };
  
  function startReset() {
  	$("#resetConfirm").show();
  }
  
  function resetCollection() {
	  $("#resetConfirm").hide();
	  $("#resetLoad").show();
	  $.ajax({
	  	type:'POST',
	  	url: "/ManagementService",
		data: {action:"reset"}
	  }).done(function( msg ) {
  			updateView();
			$("#resetLoad").hide();
		});
  }
  
  function stopReset() {
  	$("#resetConfirm").hide();
  }
  
  function editShowUnIdentified() {
	  var checked = $('#showunidentifiedcbx').is(':checked');
  	  $.ajax({
	  	type:'POST',
	  	url: "/ManagementService",
		data: {action:"editShowUnIdentified" , value:checked}
	  });
  }
  
  /*
   * Play Function
   */
  
  function playSong(data) {
	player.pause();
  	player.setSrc(data.requestPath());
	player.load();
	player.play();
 };
  
 /*
  * Collection Functions
  */ 
  
function changeCollection(coll) {
	//set Song viewmodel
  	if(coll == "all" && currentView != "all") {
		currentView = "all";
		if(allMusic_views.length == 0) {
			$.getJSON('/resources/json/AllArtists.json', function(data) {
		    	allMusic_views[0] = data.list;
		    	artists = data.list;
		    	if(artistSort) {
					artistSort = false;
					sortByArtist();
				}
   			});
			$.getJSON('/resources/json/AllAlbums.json', function(data) {
			   allMusic_views[1] = data.list;
			   albums = data.list;
			   if(albumSort) {
					albumSort = false;
					sortByAlbum();
				}
    		});
		   $.getJSON('/resources/json/AllSongs.json', function(data) {
			   allMusic_views[2] = data.list;
			   songs = data.list;
			   if(songSort) {
					songSort = false;
					sortBySong();
				}
			});
		}
		else {
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
		}
		$("#allmusicViewBtn").addClass("btnSelected");
		$("#identifiedViewBtn").removeClass("btnSelected");
		$("#unidentifiedViewBtn").removeClass("btnSelected");
		$("#nocoverViewBtn").removeClass("btnSelected");
	}
	//IDENTIFIED 
	else if (coll == "identified" && currentView != "identified") {
		currentView = "identified";
		if(identified_views.length == 0) {
			$.getJSON('/ManagementService?action=identifiedartists', function(data) {
		    	identified_views[0] = data.list;
				artists = data.list;
				if(artistSort) {
					artistSort = false;
					sortByArtist();
				}
		    }); 
			$.getJSON('/ManagementService?action=identifiedalbums', function(data) {
		    	identified_views[1] = data.list;
				albums = data.list;
				if(albumSort) {
					albumSort = false;
					sortByAlbum();
				}
		    });  
			$.getJSON('/ManagementService?action=identifiedsongs', function(data) {
		    	identified_views[2] = data.list;
				songs = data.list;
				if(songSort) {
					songSort = false;
					sortBySong();
				}
		    });   
		}
		else {
			artists = identified_views[0];
			albums = identified_views[1];
			songs = identified_views[2];
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
		$("#identifiedViewBtn").addClass("btnSelected");
		$("#unidentifiedViewBtn").removeClass("btnSelected");
		$("#nocoverViewBtn").removeClass("btnSelected");
	}
	//UNIDENTIFIED
	else if (coll == "unidentified" && currentView != "unidentified") {
		currentView = "unidentified";
		if(unidentified_views.length == 0) {
			$.getJSON('/ManagementService?action=unidentifiedartists', function(data) {
		    	unidentified_views[0] = data.list;
				artists = data.list;
				if(artistSort) {
					artistSort = false;
					sortByArtist();
				}
		    }); 
			$.getJSON('/ManagementService?action=unidentifiedalbums', function(data) {
		    	unidentified_views[1] = data.list;
				albums = data.list;
				if(albumSort) {
					albumSort = false;
					sortByAlbum();
				}
		    });  
			$.getJSON('/ManagementService?action=unidentifiedsongs', function(data) {
		    	unidentified_views[2] = data.list;
				songs = data.list;
				if(songSort) {
					songSort = false;
					sortBySong();
				}
		    });   
		}
		else {
			artists = unidentified_views[0];
			albums = unidentified_views[1];
			songs = unidentified_views[2];
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
		$("#identifiedViewBtn").removeClass("btnSelected");
		$("#unidentifiedViewBtn").addClass("btnSelected");
		$("#nocoverViewBtn").removeClass("btnSelected");
	}
	//NO COVER
	else if (coll == "nocover" && currentView != "nocover") {
		currentView = "nocover";
		if(nocover_views.length == 0) {
			$.getJSON('/ManagementService?action=nocoverartists', function(data) {
		    	nocover_views[0] = data.list;
				artists = data.list;
				if(artistSort) {
					artistSort = false;
					sortByArtist();
				}
		    }); 
			$.getJSON('/ManagementService?action=nocoveralbums', function(data) {
		    	nocover_views[1] = data.list;
				albums = data.list;
				if(albumSort) {
					albumSort = false;
					sortByAlbum();
				}
		    });  
			$.getJSON('/ManagementService?action=nocoversongs', function(data) {
		    	nocover_views[2] = data.list;
				songs = data.list;
				if(songSort) {
					songSort = false;
					sortBySong();
				}
		    });   
		}
		else {
			artists = nocover_views[0];
			albums = nocover_views[1];
			songs = nocover_views[2];
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
		$("#identifiedViewBtn").removeClass("btnSelected");
		$("#unidentifiedViewBtn").removeClass("btnSelected");
		$("#nocoverViewBtn").addClass("btnSelected");
	}
}

/*
* Sort Functions
*/

function sortByArtist() {
  	if(!artistSort) {
		artistSort = true;
		albumSort = false;
		songSort = false;
		viewModel_album.list.removeAll();
		viewModel_song.list.removeAll();
		if(query() == "" || query() == null) { 
			ko.mapping.fromJS({list:artists}, viewModel_artist);
			bindOverlays(); //bind edit button overlays
		}
		else searching(query());
		$("#artistSortBtn").addClass("btnSelected");
		$("#albumSortBtn").removeClass("btnSelected");
		$("#songSortBtn").removeClass("btnSelected");
	}
  };
  
  function sortByAlbum() {
  	if(!albumSort) {
		albumSort = true;
		artistSort = false;
		songSort = false;
		viewModel_artist.list.removeAll();
		viewModel_song.list.removeAll();
		if(query() == "" || query() == null) {
			ko.mapping.fromJS({list:albums}, viewModel_album);
			bindOverlays(); //bind edit button overlays
		}
	    else searching(query());
		$("#artistSortBtn").removeClass("btnSelected");
		$("#albumSortBtn").addClass("btnSelected");
		$("#songSortBtn").removeClass("btnSelected");
	}
  };
  
  function sortBySong() {
  	if(!songSort) {
		albumSort = false;
		artistSort = false;
		songSort = true;
		viewModel_artist.list.removeAll();
		viewModel_album.list.removeAll();
		if(query() == "" || query() == null) {
	    	ko.mapping.fromJS({list:songs}, viewModel_song);
		}
		else searching(query());
		$("#artistSortBtn").removeClass("btnSelected");
		$("#albumSortBtn").removeClass("btnSelected");
		$("#songSortBtn").addClass("btnSelected");
	}
  };
  
  /*
   * Search Function
   */ 
  var searching = function (value) {
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
	 bindOverlays(); //bind edit button overlays
  }