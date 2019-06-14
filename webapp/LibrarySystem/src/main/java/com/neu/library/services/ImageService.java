package com.neu.library.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neu.library.dao.BookDAO;
import com.neu.library.dao.ImageDAO;
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
			if(!(filetype.equals("png")||filetype.equals("jpeg")||filetype.equals("jpg"))) {
				apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "The File is of different type", "The file is of different type");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
				
			}
			if (book.getImage() != null) {
				apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "The Image already present", "The Image already present");
				return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
			}
			else {
				imageJSON = new ImageJson(this.imageDao.saveImageToLocal(file, book));
			}
		} catch (Exception e) {
			System.err.println(e);
		}

		return new ResponseEntity<Object>(imageJSON, HttpStatus.CREATED);
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
					this.imageDao.updateAttachmentFromLocal(imageId,imageToBeUpdated, file, book);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
		
}

}
