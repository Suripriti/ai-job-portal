package jar.dto;

public record LoginResponse(
        String token,
        Long id,
        String name
) {}