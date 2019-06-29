package com.neu.library.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.neu.library.model.Book;
import com.neu.library.model.Image;

@Service
public class ImageDAO {	

	@PersistenceContext
	private EntityManager entityManager;
	@Value("${cloud.islocal}")
	private boolean islocal;

	@Value("${cloud.bucketName}")
	private String bucketName;

	private static String UPLOADED_FOLDER = System.getProperty("user.dir") + "//images//";
	@Transactional
	public Image saveAttachment(MultipartFile file, Book book) {
		System.out.println(this.islocal);
		if (this.islocal) {
			return this.saveImageToLocal(file,book);
		} else {
			return this.saveImagetToS3Bucket(file,book);
		}

	}
	@Transactional
	public Image saveImageToLocal(MultipartFile file, Book book) {
		Image image = null;
		String filename;
		try {
				Files.createDirectories(Paths.get(UPLOADED_FOLDER));
				System.out.println("I am here");
				String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
				filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
						+ FilenameUtils.getExtension(file.getOriginalFilename());
				// System.out.println("filename::"+filename);

				Path path = Paths.get(UPLOADED_FOLDER + filename);
				Files.write(path, file.getBytes());
				image = new Image(path.toString(), book);
				System.out.println("Image path : " + path.toString());
				this.entityManager.persist(image);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private Image saveImagetToS3Bucket(MultipartFile file, Book book) {
		Image image = null;
		String filename;
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			//AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
			//AmazonS3Client s3Client = new AmazonS3Client(credentials);
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			
			File tempFile = this.convert(file);
			
			
			s3Client.putObject(new PutObjectRequest(this.bucketName, filename, tempFile));
			String path = s3Client.getUrl(this.bucketName, filename).toString();
			tempFile.delete();
			image = new Image(path,book);
			this.entityManager.persist(image);
		} catch (Exception e) {
		e.printStackTrace();
		}
		return image;
	}
	
	
	
	private int checkIfImage(String bookId) {
		
		int result = 0;
		try {
			Query query = this.entityManager.createQuery("SELECT COUNT(*) FROM Image i WHERE i.book_id = ?1");
			query.setParameter(1, bookId);
			Long resultInLong = (Long) query.getSingleResult();
			
			result = Math.toIntExact(resultInLong);
		} catch (Exception e) {
			
			result = 0;
		}

		return result;
}
	
	public boolean checkIfImageExsists (String bookId )
	{
		int count= this.checkIfImage(bookId);
		if (count>0)
			return true;
		else
			return false;
		
		
	}
	@Transactional
	public Image getImageFromId(String id) {
		Image imageToBeDeleted = this.entityManager.find(Image.class, id);
		
		return imageToBeDeleted;
	}
	
	public URL getImagefromS3(String id,String url ) {
		
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
	     java.util.Date expiration = new java.util.Date();
         long expTimeMillis = expiration.getTime();
         expTimeMillis += 1000 * 2 * 60;
         expiration.setTime(expTimeMillis);	
			 System.out.println("Generating pre-signed URL.");
	            GeneratePresignedUrlRequest generatePresignedUrlRequest = 
	                    new GeneratePresignedUrlRequest(this.bucketName, url.substring(url.lastIndexOf("/")+1))
	                    .withMethod(HttpMethod.GET)
	                    .withExpiration(expiration);
	            URL myUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	    
	            System.out.println("Pre-Signed URL: " +url.substring(url.lastIndexOf("/")+1,url.lastIndexOf(".")));
		
	
	return myUrl;
	}
	@Transactional
	public void deleteImage( String id) {
		if (this.islocal) {
			 this.deleteImageFromLocal(id);
		} else {
			 this.deleteImageFromS3Bucket(id);
		}

	}

	
	
	@Transactional
	public void deleteImageFromLocal(String id) 
	{
		Image imageToBeDeleted = this.entityManager.find(Image.class, id);
		boolean successfullyDeleted = deleteFromMemory(imageToBeDeleted);
		if (successfullyDeleted)
		{
			this.entityManager.remove(imageToBeDeleted);
			flushAndClear();
		}
	}
	@Transactional
	public void deleteFromDB (String id)
	{
		Image imageToBeDeleted = this.entityManager.find(Image.class, id);
		try{
			this.entityManager.remove(imageToBeDeleted);
			flushAndClear();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
}
	@Transactional
	public void deleteImageFromS3Bucket(String id) 
	{
		Image imageToBeDeleted = this.entityManager.find(Image.class, id);
		String entirePath = imageToBeDeleted.getUrl();
		String filename = entirePath.substring(entirePath.lastIndexOf("/")+1);
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			s3Client.deleteObject(this.bucketName,filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		deleteFromDB(id);
	}

	@Transactional
	public boolean deleteFromMemory(Image imageToBeDeleted) {
		String path = imageToBeDeleted.getUrl();

		try {
			java.io.File fileToBeDeleted = new java.io.File(path);
			if (fileToBeDeleted.delete()) {
				System.out.println("-------------------------------");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	@Transactional
	public Image updateAttachmentFromLocal(String id, Image image,MultipartFile file, Book book) 
	{
		//delete actual file from local
		boolean successfullyDeleted = deleteFromMemory(image);
		String filename ="";
		
		if (successfullyDeleted) 
		{ 
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			saveAttachmentToLocalMemory(file, book, filename); 
		}
		 

		Image attachmentToBeUpdated1 =null;
		if (successfullyDeleted)
		{
			//update entry from DB
			
			Path path = Paths.get(UPLOADED_FOLDER + filename);
			image = new Image(path.toString(), book);
			attachmentToBeUpdated1 =updateInDB(id, image);
			
		}

		return attachmentToBeUpdated1;
	}
	
	
	private void saveAttachmentToLocalMemory(MultipartFile file, Book book, String filename) {
		
		try {
			Files.createDirectories(Paths.get(UPLOADED_FOLDER));
			
			Path path = Paths.get(UPLOADED_FOLDER + filename);
			Files.write(path, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Transactional
	public Image updateAttachment(String id, Image image,MultipartFile file, Book book) 
	{
		if (this.islocal) {
			 return this.updateAttachmentFromLocal(id, image, file, book);
		} else {
			 return this.updateAttachmentFromS3Bucket(id, image, file,book);
		}
	}

	@Transactional
	public Image updateAttachmentFromS3Bucket(String id, Image image,MultipartFile file,Book book) 
	{
		//delete actual file from S3bucket
		Image imageToBeUpdated= this.entityManager.find(Image.class, id);
		String entirePath = imageToBeUpdated.getUrl();
		System.out.println(entirePath);
		String filename = entirePath.substring(entirePath.lastIndexOf("/")+1);
		System.out.println(filename);
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			s3Client.deleteObject(this.bucketName,filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//save new file in S3bucket
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			File tempFile = this.convert(file);
			s3Client.putObject(new PutObjectRequest(this.bucketName, filename, tempFile));
			String path = s3Client.getUrl(this.bucketName, filename).toString();
			tempFile.delete();
			image = new Image(path, book);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		//update entry from DB
		Image imageToBeUpdated1 =updateInDB(id, image);
		return imageToBeUpdated1;
}
	@Transactional
	public Image updateInDB (String id, Image image)
	{
		Image imageToBeUpdated1 = this.entityManager.find(Image.class, id);
	      imageToBeUpdated1.setUrl(image.getUrl());
		  flushAndClear();
		  return imageToBeUpdated1;
		
	}
	
	
	
	private void flushAndClear() {
		this.entityManager.flush();
		this.entityManager.clear();
	}
	
	private File convert(MultipartFile file) {
		File convFile = new File(file.getOriginalFilename());
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convFile;
}
	
	

}
