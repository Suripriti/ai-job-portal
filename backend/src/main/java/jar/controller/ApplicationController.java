package jar.controller;

import jar.entity.Application;
import jar.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    // Upload resume and calculate similarity score
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Application applyJob(
            @RequestParam Long jobId,
            @RequestParam Long userId,
            @RequestParam("resumeFile") MultipartFile resumeFile
    ) throws IOException {

        return applicationService.applyJob(jobId, userId, resumeFile);
    }
}