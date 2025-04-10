package kr.hhplus.be.server.point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.point.dto.PointChargeRequest;
import kr.hhplus.be.server.point.dto.UserPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/points")
public class PointController implements PointApiSpec {

    @GetMapping
    @Override
    public ResponseEntity<UserPointResponse> getUserPoint(@RequestParam @Positive long userId) {
        return ResponseEntity.ok(new UserPointResponse(1L, 10000));
    }

    @PostMapping("/charge")
    @Override
    public ResponseEntity<UserPointResponse> charge(@RequestBody @Valid PointChargeRequest request) {
        return ResponseEntity.ok(new UserPointResponse(1L, 10000));
    }
}
