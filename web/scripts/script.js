// JavaScript Document
var player;
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


$(document).ready(function(){
	
	player = new MediaElementPlayer('#audioPlayer');
		
   $.getJSON('/CollectionService?action=allartists', function(data) {
	   allMusic_views[0] = data.list;
	   artists = data.list;
	   viewModel_artist = ko.mapping.fromJS(data);
	   viewModel_artist.query = ko.observable('');
	   viewModel_artist.search = searching;	
	   viewModel_artist.query.subscribe(viewModel_artist.search);
	   ko.applyBindings(viewModel_artist, $("#byartist")[0]);
	   ko.applyBindings(viewModel_artist, $("#right")[0]);
	   
	   //apply overlays
	   $("button[rel]").overlay({mask: {
        color: '#fff',
        loadSpeed: 200,
        opacity: 0.5
   	 }});
   });
   
   //set Current view
   currentView = "all";
   
   //set Album viewmodel
   $.getJSON('/CollectionService?action=allalbums', function(data) {
	   allMusic_views[1] = data.list;
	   albums = data.list;
	   viewModel_album = ko.mapping.fromJS({list:[]});
	   viewModel_album.query = ko.observable('');
	   viewModel_album.search = searching;	
	   viewModel_album.query.subscribe(viewModel_album.search);
	   ko.applyBindings(viewModel_album, $("#byalbum")[0]);
	   ko.applyBindings(viewModel_album, $("#right")[0]);
    });
   //set Song viewmodel
   $.getJSON('/CollectionService?action=allsongs', function(data) {
	   allMusic_views[2] = data.list;
	   songs = data.list;
	   viewModel_song = ko.mapping.fromJS({list:[]});
	   viewModel_song.query = ko.observable('');
	   viewModel_song.search = searching;	
	   viewModel_song.query.subscribe(viewModel_song.search);
	   ko.applyBindings(viewModel_song, $("#bysong")[0]);
	   ko.applyBindings(viewModel_song, $("#right")[0]);
    });
   
 });
  

  
 
  
  
 
 