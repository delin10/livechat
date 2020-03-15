package nil.ed.livechat.web.controller;

import javax.annotation.Resource;

import java.util.List;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.service.ITagService;
import nil.ed.livechat.common.vo.TagVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */

@RestController
@RequestMapping("/livechat/tag")
public class TagController {

    @Resource
    private ITagService tagService;

    @PostMapping("/add")
    public Response<Void> addRoom(@RequestBody(required = false) TagVO tag){
        tag.setId(null);
        return tagService.addTag(tag);
    }

    @GetMapping("/list/all")
    public Response<List<TagVO>> listAll() {
        return tagService.listAll();
    }

}
