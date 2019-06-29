package com.neu.library.services;

import javax.persistence.NoResultException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neu.library.dao.BookDAO;
import com.neu.library.dao.ImageDAO;
import com.neu.library.json.BookJson;
import com.neu.library.json.ImageJson;
import com.neu.library.model.Book;
import com.neu.library.model.Image;
import com.neu.library.response.ApiResponse;

@Service
public class ImageService {
	@Autowired
	ImageDAO imageDao;
	@Autowired
	BookDAO bookDao;

	public ResponseEntity<Object> addAttachmenttoBook(String bookId, MultipartFile file) {
		ImageJson imageJSON = null;
		ApiResponse apiResponse = null;
		Book book = bookDao.getBookById(bookId);
		String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
		try {

			if (book == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Note not found", "Note not found");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			}
			if (!(filetype.equals("png") || filetype.equals("jpeg") || filetype.equals("jpg"))) {
				apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "The File is of different type",
						"The file is of different type");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);

			}
			if (book.getImage() != null) {
				apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "The Image already present",
						"The Image already present");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
			} else {
				imageJSON = new ImageJson(this.imageDao.saveAttachment(file, book));
			}
		} catch (Exception e) {
			System.err.println(e);
		}

		return new ResponseEntity<Object>(imageJSON, HttpStatus.CREATED);
	}
	
	
	public ResponseEntity<Object> delete(String bookId , String imageId) 
	{
		ApiResponse apiResponse = null;
		try {
			Book book = this.bookDao.getBookById(bookId);
			if (book == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "No such Book found", "No such Book found");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			} 
			else 
			{
				Image imageToBeDeleted = this.imageDao.getImageFromId(imageId);
				if (imageToBeDeleted == null) {
					apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Image not found", "Image not found");
					return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
				}else
				{
					//delete actual file from local/S3 bucket
					this.imageDao.deleteImage(imageId);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
		
	}
	
	public ResponseEntity<Object> getImageById(String id){
		Image image;
		ImageJson imgJson;
		
		try {
			
		image=imageDao.getImageFromId(id);
		 imgJson =new ImageJson(image.getId(),imageDao.getImagefromS3(id, image.getUrl()).toString());

		}
		catch(NoResultException e){
			ApiResponse resp = new ApiResponse(HttpStatus.NOT_FOUND, "The requested resource could not be found", "Resource not available");
			return new ResponseEntity<Object>(resp, HttpStatus.NOT_FOUND);
		}
		
		if(image != null) {
	
			return new ResponseEntity<Object>(imgJson, HttpStatus.OK);
		}
		
		else {
			ApiResponse resp = new ApiResponse(HttpStatus.NOT_FOUND, "The requested resource could not be found", "Resource not available");
			return new ResponseEntity<Object>(resp, HttpStatus.NOT_FOUND);
			
		}
}
	public ResponseEntity<Object> updateImageToBook( String bookId , String imageId,MultipartFile file) 
	{
		ApiResponse apiResponse = null;
		String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
		try {
			Book book = this.bookDao.getBookById(bookId);
			if (book == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Note not found", "Note not found");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			} 
			if(!(filetype.equals("png")||filetype.equals("jpeg")||filetype.equals("jpg"))) {
				apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "The File is of different type", "The file is of different type");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
				
			}
			else 
			{
				Image imageToBeUpdated = this.imageDao.getImageFromId(imageId);
				if (imageToBeUpdated == null) {
					apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Image not found", "Image not found");
					return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
				}else
				{
					this.imageDao.updateAttachment(imageId,imageToBeUpdated, file, book);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
		
}

}
