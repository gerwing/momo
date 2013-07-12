var player;
var query; //search query
var query2; //delayed search query
var viewModel_editAlbum;
var currentSong;
var viewModel_folders;
var editCoverAlbumID; //ID of album when editing cover

//Initialize views, collections and current
var views = new Object();
var collections = new Object();
var currentView;
var currentCollection;
var currentData; //copy of current data in use


// JavaScript Document
 $(document).ready(function(){
	
	$("#middle").hide();//hide middle
	
	//Setup View object
	var View = function View(data) {
		this.value = data;
	}
	//Define function for changing view
	View.prototype.change = function(e){
		//Only if different from current view or performing update
		if(currentView != this || e == "update") {
			//delete current viewModel data and set new current view
			if(e != "update")
				currentView.viewModel.list.removeAll();
			currentView = this;
			//load new view
			$.getJSON('/resources/json/'+ currentCollection.value + currentView.value +'.json', function(data) {
		   		currentData = data.list;
		   		if(query() == "" || query() == null) {
					ko.mapping.fromJS(data, currentView.viewModel);
					bindOverlays(); //bind edit button overlays
				}
		   		else searching(query());
	  	 	});
	  	 	//Change buttons
	  	 	if(e != "update") {
	  	 		$(e.target.parentNode).children().removeClass("btnSelected");
	  	 		$(e.target).addClass("btnSelected");
	  	 	}
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
					bindOverlays(); //bind edit button overlays
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
	collections.Identified = new Collection("Identified");
	collections.UnIdentified = new Collection("UnIdentified");
	collections.NoCover = new Collection("NoCover");
	
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
	
	player = new MediaElementPlayer('#audioPlayer');
	
	//load data
   	$.getJSON('/resources/json/'+ currentCollection.value + currentView.value +'.json', function(data) {
   	   currentData = data.list;
	   ko.mapping.fromJS(data, views.Artists.viewModel);
	   bindOverlays(); //bind edit button overlays
	   $("#middle").show();//show middle div
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
	 //determine which information is available -> Artist vs Album view
	 if(root.name != undefined)
	 	var query = data.title() + " " + root.name();
	 else 
	 	var query = data.title() + " " + data.albumArtist();
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
	currentView.change("update");
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
   * Search Function
   */ 
  var searching = function (value) {
	 //remove data in viewmodel
   	 currentView.viewModel.list.removeAll();
	 //create result array
	 results = new Array();
	 
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
	 bindOverlays(); //bind edit button overlays
  }