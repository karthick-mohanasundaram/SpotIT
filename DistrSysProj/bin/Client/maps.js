var res;
var infowindow;

//Google Map init
function initialize() {
	
	//myCenter=new google.maps.LatLng(p.coords.latitude,p.coords.longitude));
	myCenter=new google.maps.LatLng(46.227638,2.213749000000007);
	var infowindow = null;
	geocoder = new google.maps.Geocoder();
	//GOOGLE MAP
	var mapProp = {
		center:myCenter,
		zoom:5,
		mapTypeId:google.maps.MapTypeId.ROADMAP
	};
	
	map=new google.maps.Map(document.getElementById("map"),mapProp);
	
}

//Google map creation
google.maps.event.addDomListener(window, 'load', initialize);



// Callback in case of a successful geolocation call
function successCallback(position){
	clientpos= position;
	var center = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
	// using global variable:
	map.panTo(center);
	map.setZoom(13);
	$.get({
		url: "http://localhost:8080/lots/list",
		data: {
			latitude: position.coords.latitude,
			longitude: position.coords.longitude
		},
		dataType: "json" 
	}).done(DisplaySpot);
}

function maps(){
	//Geolocation call
	if(navigator.geolocation) {
	
	navigator.geolocation.getCurrentPosition(successCallback,errorCallback,{timeout:10000});
	
	}
	else{
		alert("GeoLocation must be activated !");
	}
	
}


// Callback in case of a unsuccessful geolocation call
function errorCallback(msg){
		alert("Error");
}

function ReserveSpot(){
	$.post(res.url).done(function(response){
		if( response )
			$('#customPopup').html("Booked!");
		else			
			$('#customPopup').html("No spots for you :(");
		console.log(response);
	}).fail(function(response) {
		$('#customPopup').html("Failed!");
		console.log(response);
	});
}

//Callback from asynchronous call to main server.
function DisplaySpot(data) {
	console.log(data)
	
	var markers = [];
	for(var i=0; i < data.length; i++) {
		var marker = new google.maps.Marker({
			map: map,
			position: new google.maps.LatLng(data[i].location.latitude, data[i].location.longitude)
		});
		marker.usefulData = data[i];
		marker.addListener('click', function() {
			res = this.usefulData;
			if (infowindow){
				infowindow.close();
			}
			infowindow = new google.maps.InfoWindow({
				content:"<span style=\"color: black;\" id=\"customPopup\"><a onclick=\"ReserveSpot()\" style=\"text-align:center\"> Reserve a parking is spot in "+ res.name +" </a></span>"
			});
			infowindow.open(map, this);
		});
		markers.push( marker );
	}
}


