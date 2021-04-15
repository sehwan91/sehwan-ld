import os
import skimage
import matplotlib.pyplot as plt
from skimage import data, color, io

# Class which handles the conversion from normal to line drawing
# Takes an ndarray image as parameter
class LineDraw():

    def __init__(self, image):
        self.origImage = image

    # Start of image conversion
    def convertToLine(self):
        return self.origImage



def main():
    img = io.imread('test.jpg')
    ld = LineDraw(img)

    plt.figure()
    plt.imshow(img)
    plt.figure()
    plt.imshow(ld.convertToLine())
    io.show()

if __name__ == "__main__":
    main()