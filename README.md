# Project Seam Carving

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



### Tutorial

Here's a quick tutorial on how to use the application:

![Tutorial Content](res/tutorial_content.png)


## Processing Flow Visualization

The seam carving algorithm involves several key steps that can be visualized through intermediate outputs. Below we demonstrate the complete processing pipeline using actual results from our implementation.

### Energy Map Generation

The energy map is a crucial component that identifies the importance of each pixel in the image. Areas with higher energy (brighter regions) contain more important visual information and are less likely to be removed during seam carving.

<div align="center">
  <img src="out/Energy.png" alt="Energy Map" width="400"/>
  <br>
  <em>Energy Map - Brighter areas indicate higher pixel importance</em>
</div>

### Seam Carving Results

Here are comprehensive examples demonstrating different seam carving operations with various scaling parameters and techniques:

#### Horizontal Scaling Examples

<table>
  <tr>
    <td align="center">
      <img src="res/VanGogh-starry_night.jpg" alt="Original Van Gogh" width="300"/>
      <br>
      <em>Original Image</em>
    </td>
    <td align="center">
      <img src="out/Van-Star 252-200.jpg" alt="Van Gogh Resized" width="200"/>
      <br>
      <em>Horizontally Compressed (252×200)</em>
    </td>
    <td align="center">
      <img src="out/Van-Star126x100.jpg" alt="Van Gogh Small" width="126"/>
      <br>
      <em>Further Compressed (126×100)</em>
    </td>
  </tr>
</table>

#### Vertical Scaling Examples

<table>
  <tr>
    <td align="center">
      <img src="res/Tsunami_by_hokusai_19th_century.jpg" alt="Original Tsunami" width="300"/>
      <br>
      <em>Original Hokusai's Great Wave</em>
    </td>
    <td align="center">
      <img src="out/SNCCL-222x150.jpg" alt="Tsunami Resized" width="222"/>
      <br>
      <em>Vertically Compressed (222×150)</em>
    </td>
  </tr>
</table>

#### Object Protection and Deletion

The algorithm supports mask-based protection and deletion, allowing users to preserve important objects or prioritize certain areas for removal.

<table>
  <tr>
    <td align="center">
      <img src="PicPag/PicPag/V799-protect-Mask.jpg" alt="Mask Applied" width="250"/>
      <br>
      <em>Protective Mask Applied</em>
    </td>
    <td align="center">
      <img src="out/V799-ship-protect.jpg" alt="Protected Result" width="250"/>
      <br>
      <em>Result with Object Protection</em>
    </td>
  </tr>
</table>

#### Different Scaling Ratios

<div align="center">
  <h4>Progressive Horizontal Compression</h4>
  <table>
    <tr>
      <td align="center">
        <img src="out/H1200-ship-protect.jpg" alt="Large Scale" width="300"/>
        <br>
        <em>Large Scale (1200px width)</em>
      </td>
      <td align="center">
        <img src="out/H500-ship-delete.jpg" alt="Medium Scale" width="200"/>
        <br>
        <em>Medium Scale (500px width)</em>
      </td>
      <td align="center">
        <img src="out/H300-ship-delete.jpg" alt="Small Scale" width="150"/>
        <br>
        <em>Small Scale (300px width)</em>
      </td>
    </tr>
  </table>
</div>

#### Extreme Scaling Examples

<table>
  <tr>
    <td align="center">
      <img src="out/400x266.jpg" alt="Standard Resolution" width="200"/>
      <br>
      <em>Standard Resolution (400×266)</em>
    </td>
    <td align="center">
      <img src="out/100x60pixel.jpg" alt="Extreme Compression" width="100"/>
      <br>
      <em>Extreme Compression (100×60)</em>
    </td>
  </tr>
</table>

### Algorithm Performance Analysis

The seam carving algorithm demonstrates excellent performance in preserving important visual content while achieving significant size reduction. Key observations from the results:

- **Content Preservation**: Important objects and structures remain intact even with aggressive scaling
- **Edge Preservation**: Sharp edges and high-contrast areas are well-preserved due to the energy function
- **Aspect Ratio Flexibility**: The algorithm can handle both horizontal and vertical scaling independently
- **Mask Integration**: User-defined masks effectively guide the seam selection process




