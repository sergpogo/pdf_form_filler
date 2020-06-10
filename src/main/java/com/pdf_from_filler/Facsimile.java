package com.pdf_from_filler;

public class Facsimile 
{
	private String imgPath;
	private int absolutePositionX;
	private int absolutePositionY;
	private int height;
	private int width;
	private int pageNumber;

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public int getAbsolutePositionX() {
		return absolutePositionX;
	}

	public void setAbsolutePositionX(int absolutePositionX) {
		this.absolutePositionX = absolutePositionX;
	}

	public int getAbsolutePositionY() {
		return absolutePositionY;
	}

	public void setAbsolutePositionY(int absolutePositionY) {
		this.absolutePositionY = absolutePositionY;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}	
}

