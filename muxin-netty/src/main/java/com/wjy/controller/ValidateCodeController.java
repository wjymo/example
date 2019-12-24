package com.wjy.controller;

import com.alibaba.fastjson.JSON;
import com.wjy.constant.CommonConstant;
import com.wjy.validate.code.ImageCode;
import com.wjy.validate.code.ValidateCode;
import io.netty.handler.codec.base64.Base64Encoder;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@RequestMapping("/code")
public class ValidateCodeController {
    @GetMapping("/image")
    public void createImageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = generate(new ServletWebRequest(request));
        HttpSession session = request.getSession();
        session.setAttribute(CommonConstant.IMAGE_CODE_IN_SESSION_KEY,imageCode);
        String imageCodeKey = RandomStringUtils.randomNumeric(5);
        response.setHeader("imageCodeKey",imageCodeKey);
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @GetMapping(value = "/image2",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public String getImage2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = generate(new ServletWebRequest(request));
        BufferedImage image = imageCode.getImage();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", out);
        String imageCodeKey = RandomStringUtils.randomNumeric(5);
        redisTemplate.opsForValue().set(imageCodeKey, imageCode.getCode(),180, TimeUnit.SECONDS);
        //设置允许暴露的自定义响应头，这样再跨域的场景下，前端ajax中才能拿到此响应头
        response.setHeader("Access-Control-Expose-Headers","imageCodeKey");
        response.setHeader("imageCodeKey",imageCodeKey);

//        File file = new File("C:\\Users\\Administrator\\Desktop\\timg.jpg");
//        FileInputStream inputStream = new FileInputStream(file);
//        byte[] bytes = new byte[inputStream.available()];
//        inputStream.read(bytes, 0, inputStream.available());

        byte[] bytes = out.toByteArray();
        byte[] base64Bytes = Base64.encodeBase64(bytes);
        String base64 = new String(base64Bytes);
        return "data:image/jpeg;base64,".concat(base64);
    }
    public byte[] imageToBase64ByLocal(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try (InputStream in = new FileInputStream(imgFile)) {
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
        byte[] encode = Base64.encodeBase64(data);
        return encode;// 返回Base64编码过的字节数组字符串
    }

    @GetMapping(value = "/image3",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public BufferedImage  getImage3() throws IOException {
        return ImageIO.read(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\timg.jpg")));
    }

    @GetMapping(value = "/test")
    @ResponseBody
    public String  test()  {
        System.out.println(1);
        return "huyao";
    }



    @GetMapping("/sms")
    public String createSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = RandomStringUtils.randomNumeric(6);
//        ValidateCode validateCode = new ValidateCode(code, 180);
//        HttpSession session = request.getSession();
//        session.setAttribute(CommonConstant.SMS_CODE_IN_SESSION_KEY,validateCode);
        String smsCodeKey = RandomStringUtils.randomNumeric(5);
        redisTemplate.opsForValue().set(smsCodeKey, code,180, TimeUnit.SECONDS);
        //设置允许暴露的自定义响应头，这样再跨域的场景下，前端ajax中才能拿到此响应头
        response.setHeader("Access-Control-Expose-Headers","smsCodeKey");
        response.setHeader("smsCodeKey",smsCodeKey);
        String mobile = request.getParameter(CommonConstant.DEFAULT_PARAMETER_NAME_MOBILE);
        log.info("向手机"+mobile+"发送短信验证码"+code);
        return code;
    }


    public ImageCode generate(ServletWebRequest request) {
        BufferedImage image = new BufferedImage(67, 23, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, 67, 23);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(67);
            int y = random.nextInt(23);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        String sRand = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }

        g.dispose();
        return new ImageCode(image, sRand, 180);
    }
    /**
     * 生成随机背景条纹
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
