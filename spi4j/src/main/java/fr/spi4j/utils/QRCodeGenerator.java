/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Utility class for QR code generation.
 * @author MinArm
 */
public class QRCodeGenerator
{

   /**
    * Content to encode in QR code.
    */
   private final String _content;

   /**
    * The extension for the image (".png", ".jpg", etc...).
    */
   private final String _imageFormat;

   /**
    * The name for the file being generated.
    */
   private final String _outputFileName;

   /**
    * The path for the file being generated.
    */
   private String _outputFilePath;

   /**
    * The level of correction to avoid bas scan.
    */
   private final ErrorCorrectionLevel _errorCorrectionLevel;

   /**
    * The color for the QR code.
    */
   private final int _color;

   /**
    * The size (L*H) for the image.
    */
   private final int _size;

   /**
    * Does we user want a gradient for the QR code ?
    */
   private final boolean _gradient;

   /**
    * Private constructor.
    */
   private QRCodeGenerator (final String p_content, final String p_imageFormat, final String p_outputFileName,
            final String p_outputFilePath, final ErrorCorrectionLevel p_errorCorrectionLevel, final int p_size,
            final int p_color, final boolean p_gradient)
   {

      _size = p_size;
      _color = p_color;
      _content = p_content;
      _gradient = p_gradient;
      _imageFormat = p_imageFormat;
      _outputFileName = p_outputFileName;
      _outputFilePath = p_outputFilePath;
      _errorCorrectionLevel = p_errorCorrectionLevel;

   }

   /**
    * Generation for the QR code.
    * @throws WriterException
    *            : Writing QR code error.
    * @throws IOException
    *            : File writing error.
    */
   private void generateQRCode () throws WriterException, IOException
   {

      final QRCode v_qr = new QRCode();
      Encoder.encode(_content, _errorCorrectionLevel, v_qr);
      final ByteMatrix v_matrix = v_qr.getMatrix();

      final Area v_a = new Area();
      final Area v_module = new Area(new Rectangle.Float(0, 0, 1, 1));
      final AffineTransform v_at = new AffineTransform();
      final int v_width = v_matrix.getWidth();

      for (int v_i = 0; v_i < v_width; v_i++)
      {
         for (int v_j = 0; v_j < v_width; v_j++)
         {
            if (v_matrix.get(v_j, v_i) == 1)
            {
               v_a.add(v_module);
            }
            v_at.setToTranslation(1, 0);
            v_module.transform(v_at);
         }
         v_at.setToTranslation(-v_width, 1);
         v_module.transform(v_at);
      }

      double v_ratio = _size / (double) v_width;

      // Quietzone : 4 border modules around the QR Code (empty area for good
      // identification of the code)
      final double v_adjustment = v_width / (double) (v_width + 8);
      v_ratio = v_ratio * v_adjustment;
      v_at.setToTranslation(4, 4);
      v_a.transform(v_at);

      // We enlarge the circumference to the desired size.
      v_at.setToScale(v_ratio, v_ratio);
      v_a.transform(v_at);

      // Java 2D Image processing.
      final BufferedImage v_im = new BufferedImage(_size, _size, BufferedImage.TYPE_INT_RGB);
      final Graphics2D v_g = (Graphics2D) v_im.getGraphics();

      // Gradient.
      final Color color1 = new Color(_color);
      Color color2 = new Color(_color);
      if (_gradient)
      {
         // Very basic to be optimized.
         color2 = new Color(0xFFFFFF);
      }

      // Begin / End for the gradient.
      final float[] fractions =
      {0.0f, 1.0f };
      final Color[] colors =
      {color1, color2 };
      v_g.setPaint(new RadialGradientPaint(_size / 2, _size / 2, _size / 2, fractions, colors));

      // White backgroud
      v_g.setBackground(new Color(0xFFFFFF));
      v_g.clearRect(0, 0, _size, _size);

      // Fill the modules.
      v_g.fill(v_a);

      if (_outputFilePath.lastIndexOf(File.separator) != _outputFilePath.length())
      {
         _outputFilePath += File.separator;
      }

      // Write the generated QR code on the disk.
      final File f = new File(_outputFilePath + _outputFileName + "." + _imageFormat);
      f.setWritable(true);
      ImageIO.write(v_im, _imageFormat, f);
      f.createNewFile();
   }

   /**
    * Specific builder for the QRCodeGenerator.
    * @author MINARM
    */
   public static class Builder
   {
      /**
       * Content to encode in QR code.
       */
      private String _content;

      /**
       * The extension for the image (".png", ".jpg", etc...).
       */
      private String _imageFormat;

      /**
       * The name for the file being generated.
       */
      private String _outputFileName;

      /**
       * The path for the file being generated.
       */
      private String _outputFilePath;

      /**
       * The level of correction to avoid bas scan.
       */
      private ErrorCorrectionLevel _errorCorrectionLevel;

      /**
       * The size (L*H) for the image.
       */
      private int _size;

      /**
       * The color for the QR code.
       */
      private int _color;

      /**
       * Does we user want a gradient for the QR code ?
       */
      private boolean _gradient;

      /**
       * Set the content to be encoded.
       * @param p_content
       *           : The content to be encoded.
       * @return The builder.
       */
      public Builder withContent (final String p_content)
      {
         _content = p_content;
         return this;
      }

      /**
       * Set the format (extension) for the image to be generated.
       * @param p_imageFormat
       *           : The format for the image code.
       * @return The builder./
       */
      public Builder withImageFormat (final String p_imageFormat)
      {
         _imageFormat = p_imageFormat;
         return this;
      }

      /**
       * Set the name for the image file to be generated.
       * @param p_outputFileName
       *           : The file name for the image file.
       * @return The builder.
       */
      public Builder withOutputFileName (final String p_outputFileName)
      {
         _outputFileName = p_outputFileName;
         return this;
      }

      /**
       * Set the path to the image file to be generated.
       * @param p_outputFilePath
       *           : The path for the image file.
       * @return The builder.
       */
      public Builder withOutputFilePath (final String p_outputFilePath)
      {
         _outputFilePath = p_outputFilePath;
         return this;
      }

      /**
       * Set the level of correction to avoid bad scan.
       * @param p_errorCorrectionLevel
       *           : The level of correction.
       * @return The builder.
       */
      public Builder withErrorCorrectionLevel (final ErrorCorrectionLevel p_errorCorrectionLevel)
      {
         _errorCorrectionLevel = p_errorCorrectionLevel;
         return this;
      }

      /**
       * Set the size for the image code.
       * @param p_size
       *           : The size (L*H) for the image.
       * @return The builder.
       */
      public Builder withSize (final int p_size)
      {
         _size = p_size;
         return this;
      }

      /**
       * Set the color for the QR code.
       * @param p_color
       *           : The color for the QR code.
       * @return The builder.
       */
      public Builder withColor (final int p_color)
      {
         _color = p_color;
         return this;
      }

      /**
       * Set a gradient for the QR code.
       * @param p_gradient
       *           : If true, set a gradient for the QRCode.
       * @return The builder.
       */
      public Builder withGradient (final boolean p_gradient)
      {
         _gradient = p_gradient;
         return this;
      }

      /**
       * @throws WriterException
       *            : Writing QR code error.
       * @throws IOException
       *            : File writing error.
       */
      public void build () throws WriterException, IOException
      {

         if (null == _content)
         {
            _content = "NON CONTENT";
         }

         if (0 == _size)
         {
            _size = 300;
         }

         if (0 == _color)
         {
            _color = 0x000000;
         }

         if (null == _errorCorrectionLevel)
         {
            _errorCorrectionLevel = ErrorCorrectionLevel.M;
         }

         if (null == _imageFormat)
         {
            _imageFormat = "png";
         }

         final QRCodeGenerator v_qrCode = new QRCodeGenerator(_content, _imageFormat, _outputFileName, _outputFilePath,
                  _errorCorrectionLevel, _size, _color, _gradient);

         v_qrCode.generateQRCode();
      }
   }
}
