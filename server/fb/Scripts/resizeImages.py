# resize an image using the PIL image library
# free from:  http://www.pythonware.com/products/pil/index.htm
# tested with Python24        vegaseat     11oct2005
from PIL import Image
import os
filesFolder = "../../images/imagesStack/";
dirList=os.listdir(filesFolder)

for ind in range(len(dirList)):
	if ( dirList[ind].lower().find(".jpg") == -1 or dirList[ind].lower().find("thumb") <> -1 ):
		print dirList[ind]
		continue;
# open an image file (.bmp,.jpg,.png,.gif) you have in the working folder
	imageFile = filesFolder +  dirList[ind];
	im1 = Image.open(imageFile)
	# adjust width and height to your needs
	height_thumb = 64;
	width_thumb = 64;
	# use one of these filter options to resize the image
	im1.thumbnail((width_thumb, height_thumb), Image.ANTIALIAS)    # best down-sizing filter
	ext = ".jpg"

	im1.save( filesFolder + "thumbs/"+ dirList[ind])
