/**
 * 
 */

$(function(){
	
	
	//Show all users in the database
	$("#showUsersForm").submit(function(e){
		
		e.preventDefault();
		var requestData = {
				"type":"GET",
				"dataType":"json",
				"url":"/FinalProject/rs/newsletter/subscribers"
		};
		
		var response = $.ajax(requestData);
		
		response.done(function(responseData, status, jqXHR){
			var list = $("#emailTableBody");
			
			if(responseData != null)
			{
				var array = responseData.subscriberList;
				list.empty();
			
				$.each(array, function(i,obj){
					list.append("<tr><td>" + obj.subscriber.subscriberName + "</td><td>" + obj.subscriber.subscriberEmail + "</td></tr>");
				});
			}
			
		});
		
		response.fail(function(jqXHR,status,error){
			alert("Error getting all subscribers");
		});
	});
	
	
	//Add a new user to the database
	$("#addForm").submit(function(e){
		
		e.preventDefault();
		var name = $("#addNameTextBox").val();
		$("#addNameTextBox").val("");
		var email = $("#addEmailTextBox").val();
		$("#addEmailTextBox").val("");
		
		if(name && email)
		{
			var listItem = ({"subscriber": {"subscriberName": name,
											"subscriberEmail": email}});
			
			$.ajax({
				type: "POST",
				url: "/FinalProject/rs/newsletter/subscribers",
				data: JSON.stringify(listItem),
				contentType: "application/json",
				dataType: 'json',
				complete: function(jqXHR, textStatus){$("#showUsersForm").submit();},
				failure: function(errMsg){alert("Error posting subscriber");}
			});
	
		
		}
	});
	
	//Remove a user from the database
	$("#removeForm").submit(function(e){
		
		e.preventDefault();
		var email = $("#removeTextBox").val();
		$("#removeTextBox").val("");
		if(email)
		{
			$.ajax({
				type: "DELETE",
				url: "/FinalProject/rs/newsletter/subscribers/" + email,
				complete: function(jqXHR, textStatus){$("#showUsersForm").submit();},
				failure: function(errMsg){alert("Error deleting subscriber");}
			});
		}
	});
	
	//Update a users email address
	$("#updateForm").submit(function(e){
		e.preventDefault();
		
		var oldEmail = $("#oldEmailTextBox").val();
		var newEmail = $("#newEmailTextBox").val();
		$("#oldEmailTextBox").val("");
		$("#newEmailTextBox").val("");
		
		
		if(oldEmail && newEmail){			
			$.ajax({
				type: "PUT",
				url: "/FinalProject/rs/newsletter/subscribers/"+ oldEmail + "/" + newEmail,
				complete: function(jqXHR, textStatus){$("#showUsersForm").submit();},
				failure: function(errMsg){alert("Error Updating subscriber's email address");}
			});	
		}
	});
	
	//Send email to all addresses in the database
	$("#sendEmailForm").submit(function(e){
		
		e.preventDefault();
		$.ajax({
			type: "POST",
			url: "/FinalProject/rs/newsletter/email",
			failure: function(errMsg){alert("Error emailing subscribers");}
		});
	});
	
	
});
