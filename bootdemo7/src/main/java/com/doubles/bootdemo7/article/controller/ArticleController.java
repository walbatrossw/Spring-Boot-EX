package com.doubles.bootdemo7.article.controller;

import com.doubles.bootdemo7.article.domain.Article;
import com.doubles.bootdemo7.article.persistence.ArticleRepository;
import com.doubles.bootdemo7.article.persistence.CustomCrudRepository;
import com.doubles.bootdemo7.article.vo.PageMaker;
import com.doubles.bootdemo7.article.vo.PageVO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log
@Controller
@RequestMapping("/article")
public class ArticleController {

//    private final ArticleRepository articleRepository;
//
//    @Autowired
//    public ArticleController(ArticleRepository articleRepository) {
//        this.articleRepository = articleRepository;
//    }

    private final CustomCrudRepository articleRepository;

    @Autowired
    public ArticleController(CustomCrudRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // 게시물 목록
    @GetMapping("/list")
    public void getArticles(PageVO pageVO, Model model) {

        Pageable page = pageVO.makePageable(0, "articleNo");
        Page<Object[]> result = articleRepository.getCustomPage(pageVO.getType(), pageVO.getKeyword(), page);

        log.info("" + page);
        log.info("" + result);

        log.info("Total Page Number : " + result.getTotalPages());

        model.addAttribute("result", new PageMaker<>(result));

    }

    // 게시물 작성 페이지
    @GetMapping("/write")
    public void write(@ModelAttribute("article") Article article) {
        log.info("write get");
    }

    // 게시물 작성 처리
    @PostMapping("/write")
    public String write(@ModelAttribute("articles") Article article, RedirectAttributes redirectAttributes) {
        log.info("write post");
        log.info("" + article);

        articleRepository.save(article);
        redirectAttributes.addFlashAttribute("msg", "write success");
        return "redirect:/article/list";
    }

    // 게시물 조회
    @GetMapping("/read")
    public void read(Long articleNo, @ModelAttribute("pageVO") PageVO pageVO, Model model) {

        articleRepository.findById(articleNo).ifPresent(article -> model.addAttribute("article", article));
        log.info("read");

    }

    // 게시물 수정 페이지
    @GetMapping("/modify")
    public void modify(Long articleNo, @ModelAttribute("pageVO") PageVO pageVO, Model model) {

        articleRepository.findById(articleNo).ifPresent(article -> model.addAttribute("article", article));
        log.info("modify get");

    }

    // 게시물 수청 처리
    @PostMapping("/modify")
    public String modify(Article article, PageVO pageVO, RedirectAttributes redirectAttributes) {
        log.info("modify post");

        articleRepository.findById(article.getArticleNo()).ifPresent(origin -> {
            origin.setTitle(article.getTitle());
            origin.setContent(article.getContent());
            articleRepository.save(origin);
            redirectAttributes.addFlashAttribute("msg", "modify success");
            redirectAttributes.addAttribute("articleNo", origin.getArticleNo());
        });

        getPages(pageVO, redirectAttributes);

        return "redirect:/article/read";
    }

    // 게시물 삭제 처리
    @PostMapping("/remove")
    public String remove(Long articleNo, PageVO pageVO, RedirectAttributes redirectAttributes) {

        articleRepository.deleteById(articleNo);
        redirectAttributes.addFlashAttribute("msg", "remove success");
        getPages(pageVO, redirectAttributes);
        log.info("REMOVE Article : " + articleNo);

        return "redirect:/article/list";
    }

    private void getPages(PageVO pageVO, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("page", pageVO.getPage());
        redirectAttributes.addAttribute("size", pageVO.getSize());
        redirectAttributes.addAttribute("type", pageVO.getType());
        redirectAttributes.addAttribute("keyword", pageVO.getKeyword());
    }
}
