package stitch.crew.hour.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
