package com.tangcheng.learning.awt;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * spring-boot-cookbook
 *
 * @author tangcheng
 * @date 6/25/2018 12:03 AM
 */
@Slf4j
public class VotePosterBuilder {

    private BufferedImage templateImage = null;
    private Graphics2D graphics2D;
    private int templateWidth;
    private int templateHeight;

    public VotePosterBuilder(String templateUrl) {
        try {
            URL url = new URL(templateUrl);
            templateImage = ImageIO.read(url.openStream());
            graphics2D = templateImage.createGraphics();
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics2D.setRenderingHints(rh);
            templateWidth = templateImage.getWidth();
            templateHeight = templateImage.getHeight();
        } catch (IOException e) {
            log.error("fail to read templateUrl:{}", templateUrl, e);
        }
    }

    public VotePosterBuilder addQrcode(String qrCodeImageUrl) throws IOException {
        URL url = new URL(qrCodeImageUrl);
        BufferedImage qrcodeImage = ImageIO.read(url.openStream());
        int x = templateWidth * 3 / 8;
        int y = templateHeight * 3 / 4;
        int width = templateWidth / 4;
        int height = templateWidth / 4;
        graphics2D.drawImage(qrcodeImage, x, y, width, height, null);
        return this;
    }

    public VotePosterBuilder addHeadImage(String headImageUrl) throws IOException {
        URL url = new URL(headImageUrl);
        BufferedImage headImage = ImageIO.read(url.openStream());
        int x = 470;
        int y = 520;
        int width = 190;
        int height = 190;


        Shape olcClip = graphics2D.getClip();
        Ellipse2D.Double shape = new Ellipse2D.Double(x, y, width, height);
        // graphics2D.fill(new Rectangle(templateWidth, templateWidth));
        graphics2D.clip(shape);
        headImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        graphics2D.drawImage(headImage, x, y, null);
        graphics2D.setColor(Color.WHITE);
        shape = new Ellipse2D.Double(x, y, width - 1, height - 1);
//        graphics2D.drawOval(x, y, width, height);
        graphics2D.draw(shape);
        graphics2D.setClip(olcClip);
//        graphics2D.drawImage(headImage, x - width - 10, y, width, height, null);
        return this;
    }

    public VotePosterBuilder addNickname(String nickname) {
        Font font = new Font("Serif", Font.BOLD, 50);
        Rectangle2D bounds = font.getStringBounds(nickname, graphics2D.getFontRenderContext());
        double x = (templateWidth - bounds.getWidth()) / 2;
        double y = (templateHeight - bounds.getHeight()) / 2;
        double ascent = -bounds.getY();
        double baseY = y + ascent - 50;
        log.info("x:{},baseY:{}", x, baseY);
        graphics2D.setFont(font);
        graphics2D.setColor(Color.blue);//设置当前绘图颜色
        graphics2D.drawString(nickname, (int) x, (int) baseY);
        return this;
    }

    public void build() {
        graphics2D.dispose();
        templateImage.flush();
        try {
            File output = new File("poster.jpg");
            ImageIO.write(templateImage, "jpg", output);
            log.info("output:{}", output.getAbsolutePath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        System.gc();
    }

}

