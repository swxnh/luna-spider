package com.luna.zhihusearch.controller;

import com.luna.zhihusearch.service.AssociationalWordServer;
import com.luna.common.utils.ResponseResult;
import com.luna.common.vo.zhihusearch.AssociationalWordVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 关联词控制器
 * 允许所有跨域
 * @author 文轩
 */
@RestController
@RequestMapping("/associationalWord")
@CrossOrigin(origins = "*")
public class AssociationalWordController {

    private final AssociationalWordServer associationalWordServer;

    public AssociationalWordController(AssociationalWordServer associationalWordServer) {
        this.associationalWordServer = associationalWordServer;
    }

    /**
     * 获取关联词
     */
    @RequestMapping("/getAssociationalWord")
    public ResponseResult<List<AssociationalWordVO>> getAssociationalWord(@RequestParam String keyword) {
        return ResponseResult.ok(associationalWordServer.getAssociationalWord(keyword));

    }
}
