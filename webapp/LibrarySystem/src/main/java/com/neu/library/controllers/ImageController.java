package com.neu.library.controllers;

import java.io.IOException;
import java.util.logging.FileHandler;

import javax.validation.constraints.NotNull;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neu.library.response.ApiResponse;
import com.neu.library.services.ImageService;
import com.neu.library.services.StatMetric;

@RestController
public class ImageController {
	@Autowired
	ImageService imageService;
	@Autowired
	 StatMetric statsMetric;

	
	private static final Logger logger = LoggerFactory.getLogger(BookController.class);
	 

	@RequestMapping(value = "/book/{bookId}/image", method = RequestMethod.POST)
	public ResponseEntity<Object> imageToBook(@PathVariable @NotNull String bookId,
			@RequestParam("file") MultipartFile file) throws SecurityException, IOException {
		  fh = new FileHandler("/opt/tomcat/logs/csye6225.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	        logger.info("Adding image to book");  
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.imageService.addAttachmenttoBook(bookId, file);
}

	@RequestMapping(value = "/book/{bookId}/image/{idImage}", method = RequestMethod.PUT)
	public ResponseEntity<Object> upadateImageToBook(@PathVariable("bookId") @NotNull String bookId, @PathVariable("idImage") @NotNull String imageId, @RequestParam("file") MultipartFile file) throws SecurityException, IOException {
		  fh = new FileHandler("/opt/tomcat/logs/csye6225.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	        logger.info("Update image to book");  
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.imageService.updateImageToBook(bookId, imageId,file);
	}
	@RequestMapping(value = "/book/{bookId}/image/{idImage}", method = RequestMethod.GET)
	public ResponseEntity<Object> getImageToBook(@PathVariable("bookId") @NotNull String bookId, @PathVariable("idImage") @NotNull String imageId, @RequestParam("file") MultipartFile file) throws SecurityException, IOException {
		  fh = new FileHandler("/opt/tomcat/logs/csye6225.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	        logger.info("Fetching image");  
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.imageService.getImageById(imageId);
	}
	
	@RequestMapping(value = "/book/{bookId}/image/{imageId}", method = RequestMethod.DELETE	)
	public ResponseEntity<Object> deleteImage(@PathVariable("bookId") @NotNull String bookId, @PathVariable("imageId") @NotNull String imageId) throws SecurityException, IOException {
		  fh = new FileHandler("/opt/tomcat/logs/csye6225.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	        logger.info("Delete image to book");  
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}

		
		return this.imageService.delete(bookId, imageId);
	}
	
	
	

}
