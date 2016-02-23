package com.example.springboot.app;


import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


// Trivial application that models Reservations

/* For a custom health indicator:
 * @Bean
	HealthIndicator healthIndicator(){
		return () -> Health.status("Text here").build();
	
	*/
	
@Bean
CommandLineRunner runner (ReservationRepository rr){
		return args ->{
									
			Arrays.asList("Lautaro,Carlos,Luis,Leandro,Maria".split(","))
				.forEach(n -> rr.save(new Reservation(n)));
			
			rr.findAll().forEach(System.out::println);
			
			rr.findByReservationName("Lautaro").forEach(System.out::println);
		};
	}

}

/*
 * This is the "long" way of doing a Rest...
@Controller
class ReservationMvcController {
	@RequestMapping("/reservations.php")
	String reservations (Model model){
		model.addAttribute("reservations", this.reservationRepository.findAll());
		
		return "reservations";
	}
	
	@Autowired
	private ReservationRepository reservationRepository;
}

*/

@RestController
class ReservationRestController{
	@RequestMapping("/reservations")
	Collection<Reservation> reservations(){
		return this.reservationRepository.findAll();
	}
	
	
	@Autowired	
	private ReservationRepository reservationRepository;
}

@Component // Gets automatically plugged into the pipeline
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>>{

	@Override
	public Resource<Reservation> process(Resource<Reservation> reservationResource) {
		// TODO Auto-generated method stub
		reservationResource.add(new Link("http://s3.com/imgs/" + reservationResource.getContent().getId()+".jpg","profile-photo"));
		return reservationResource;
	}
	
}

@Controller
class ReservationMvcController{
	@RequestMapping("/reservations.php")
	String reservations (Model model){
		model.addAttribute("reservations", this.reservationRepository.findAll());
		return "reservations"; // Will look at: src/main/resources/templates/ + $X + .html
	}
	
	@Autowired // Not recommended to do field injection
	private ReservationRepository reservationRepository;
	
}
@RepositoryRestResource // This is the way to do it "native"
interface ReservationRepository extends JpaRepository <Reservation,Long>{
	
	// Equivalent to: select * from Reservation where reservationName = rn 
	Collection<Reservation> findByReservationName (String rn);
}

@Entity
class Reservation {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String reservationName;
	
	
	Reservation(){
		
	}
		
	
	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

	@Override
	public String toString() {
		return "Reservations [id=" + id + ", reservationName=" + reservationName + "]";
	}

}


