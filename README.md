# SeamCarver-SUSTech-DSAA-B2024Spring

A Course Project in SUSTech, cooperate with Cao Zhengyang

## Overview

This project implements a Seam Carving application in Java, providing a graphical user interface (GUI) for intelligent image resizing. Seam Carving is a content-aware image resizing technique that allows for the reduction or expansion of images by intelligently removing or inserting 'seams' (paths of least importance) without distorting the main content of the image. This application includes features for both shrinking and expanding images, along with tools for visualizing energy maps and protecting/deleting specific regions using masks.

## Features

*   **Content-Aware Image Resizing**: Intelligently resizes images by removing or adding seams, preserving important visual content.
*   **Horizontal and Vertical Resizing**: Supports resizing along both horizontal and vertical axes.
*   **Energy Map Visualization**: Displays the energy map of the image, helping users understand the importance of different pixels.
*   **Mask-Based Protection/Deletion**: Allows users to mark areas for protection (to prevent them from being removed) or deletion (to prioritize their removal) using a brush tool.
*   **Interactive GUI**: User-friendly interface for importing images, adjusting resizing parameters, and visualizing results.
*   **Image Import/Export**: Supports importing various image formats and exporting the processed images.

## Getting Started

### Prerequisites

To run this application, you will need:

*   Java Development Kit (JDK) 8 or higher
*   An IDE such as Visual Studio Code or IntelliJ IDEA (optional, but recommended for development)

### Running the Application

1.  **Clone the repository** (or extract the provided `ProjectSeamCarving.zip` file):

    ```bash
    git clone <repository_url>
    cd ProjectSeamCarving
    ```

2.  **Compile the Java source files**:

    Navigate to the project root directory and compile the `.java` files. If you are using an IDE, it will typically handle compilation automatically.

    ```bash
    javac -d bin src/*.java
    ```

3.  **Run the application**:

    Execute the `Run.java` file, which contains the main entry point for the GUI.

    ```bash
    java -cp bin Run
    ```

    Alternatively, if running from an IDE, simply run the `Run.java` file.

## Usage

Upon launching the application, you will be presented with the Image Editor GUI:

1.  **Import Image**: Click on `Menu` -> `Upload` to select an image file (JPG, JPEG, PNG) from your system.
2.  **Adjust Resizing**: Use the horizontal and vertical sliders to specify the desired reduction or expansion amount. The resolution indicator will show the current image dimensions.
3.  **Brush Tool**: Use the brush tool to mark areas for protection (red) or deletion (green). Adjust the brush size using the slider.
    *   **Note**: The brush tool should be used on the initial image or after resetting.
4.  **View Maps**: You can view the `E-Map` (Energy Map) and `Mask View` from the `Menu` to understand the algorithm's internal workings and your mask's effect.
5.  **Confirm Changes**: Click the `Confirm` button to apply the seam carving operation. You may click it multiple times for different results due to the probabilistic nature of seam selection.
6.  **Download Image**: After processing, click `Menu` -> `Download` to save the modified image.

## Algorithm Details

The Seam Carving algorithm implemented in this project follows these general steps:

1.  **Image Loading and Representation**: The input image is loaded and represented as a pixel matrix.
2.  **Energy Function Calculation**: An energy function (e.g., gradient-based) is applied to the image to quantify the importance of each pixel. Pixels with higher energy are considered more important.
3.  **Dynamic Programming for Seam Finding**: Dynamic programming is used to find the optimal seam (a path of connected pixels from one end of the image to the other) with the minimum total energy. This ensures that the least important pixels are targeted for removal or insertion.
4.  **Seam Removal/Insertion**: Once a seam is identified, pixels along this seam are either removed (for shrinking) or duplicated (for expanding), and the image is reconstructed.
5.  **Iteration**: Steps 2-4 are repeated until the desired image dimensions are achieved.

## Project Structure

```
ProjectSeamCarving/
├── src/                  # Source code files
│   ├── Client.java
│   ├── ImageArrayList.java
│   ├── ImageEditorGUI.java   # Main GUI class
│   ├── ImagePreprocessor.java
│   ├── RestrictedNumberField.java
│   ├── Run.java              # Entry point for the application
│   └── SeamCarver.java
├── lib/                  # External libraries (if any)
├── bin/                  # Compiled class files
├── res/                  # Resources (images, icons, etc.)
├── out/                  # Output directory for processed images
├── PicPag/               # Sample images and test cases
├── README.md             # This README file
└── Plan.md               # Project development plan and ideas
```

## Collaboration

This project was developed in collaboration with Cao Zhengyang as a course project at SUSTech.

## License

[Optional: Add license information here, e.g., MIT License, Apache 2.0 License, etc.]

## Acknowledgements

*   Special thanks to the course instructors and TAs at SUSTech for their guidance.
*   Inspired by the original Seam Carving algorithm by Shai Avidan and Ariel Shamir.



