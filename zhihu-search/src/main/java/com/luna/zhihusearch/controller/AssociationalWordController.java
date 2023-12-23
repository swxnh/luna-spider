package com.luna.zhihusearch.controller;

import com.luna.common.anno.ZhihuSystemLog;
import com.luna.common.enmu.Method;
import com.luna.common.enmu.Module;
import com.luna.zhihusearch.service.AssociationalWordServer;
import com.luna.common.utils.ResponseResult;
import com.luna.common.vo.zhihusearch.AssociationalWordVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关联词控制器
 * 允许所有跨域
 * @author 文轩
 */
@RestController
@RequestMapping("/associationalWord")
@CrossOrigin(origins = "*")
@ZhihuSystemLog(model = Module.ASSOCIATIONAL_WORD)
public class AssociationalWordController {

    private final AssociationalWordServer associationalWordServer;

    public AssociationalWordController(AssociationalWordServer associationalWordServer) {
        this.associationalWordServer = associationalWordServer;
    }

    /**
     * 获取关联词
     */
    @ZhihuSystemLog(method = Method.SEARCH)
    @GetMapping("")
    public ResponseResult<List<AssociationalWordVO>> getAssociationalWord(@RequestParam String keyword) {
        return ResponseResult.ok(associationalWordServer.getAssociationalWord(keyword));

    }
}
