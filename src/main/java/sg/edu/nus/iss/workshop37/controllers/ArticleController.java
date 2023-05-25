package sg.edu.nus.iss.workshop37.controllers;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import sg.edu.nus.iss.workshop37.services.ArticleService;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class ArticleController {

    @Autowired
    private ArticleService articleSvc;

    @PostMapping(path = "/article")
    public String createArticle(@RequestPart String title, @RequestPart String content,
            @RequestPart MultipartFile imageFile, Model model, HttpSession session) throws IOException {

        int postId = articleSvc.postArticle(title, content, imageFile);
        model.addAttribute("postId", postId);
        model.addAttribute("uploaded", postId > 0 ? true : false);
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("imageSrc", "/image/%s".formatted(postId));
        model.addAttribute("image64", convertImageToBase64(articleSvc.getArticleImage(postId)));

        StringBuilder sb = new StringBuilder();
        sb.append("data:image/png;base64,").append(convertImageToBase64(articleSvc.getArticleImage(postId)));
        model.addAttribute("imageSB", sb.toString());

        session.setAttribute("image", articleSvc.getArticleImage(postId));
        return "article";
    }

    @GetMapping(path = "/image")
    @ResponseBody
    public byte[] getArticleImageBySession(HttpSession session) {
        byte[] image = (byte[]) session.getAttribute("image");
        session.invalidate();
        return image;
    }

    @GetMapping(path = "/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getArticleImagePathVariable(@PathVariable int id) {
        byte[] img = articleSvc.getArticleImage(id);
        if (img == null) {
            return ResponseEntity.notFound().build();
        }
        ;

        return ResponseEntity.status(HttpStatus.OK).body(img);
    }

    public String convertImageToBase64(byte[] image) {
        return Base64.getEncoder().encodeToString(image);
    }
}
