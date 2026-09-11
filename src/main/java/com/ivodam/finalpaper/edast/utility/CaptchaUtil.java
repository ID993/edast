package com.ivodam.finalpaper.edast.utility;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public final class CaptchaUtil {

  private CaptchaUtil() {}

  public static Captcha createCaptcha(Integer width, Integer height) {
    return new Captcha.Builder(width, height)
        .addBackground(new GradiatedBackgroundProducer())
        .addText(new DefaultTextProducer(), new DefaultWordRenderer())
        .addNoise(new CurvedLineNoiseProducer())
        .build();
  }

  public static String encodeCaptcha(Captcha captcha) {
    try (var output = new ByteArrayOutputStream()) {
      if (!ImageIO.write(captcha.getImage(), "jpg", output)) {
        throw new IllegalStateException("JPEG image writer is unavailable");
      }

      return Base64.getEncoder().encodeToString(output.toByteArray());
    } catch (IOException exception) {
      throw new IllegalStateException("Could not encode CAPTCHA image",
                                      exception);
    }
  }
}