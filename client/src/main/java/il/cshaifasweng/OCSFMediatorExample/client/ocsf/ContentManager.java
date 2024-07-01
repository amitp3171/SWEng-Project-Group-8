package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import java.time.LocalTime;

public class ContentManager extends AbstractEmployee {

    public ContentManager(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password);
    }
    public void RemoveFromMovieList(Branch branch, InTheaterMovie movie){
        branch.getInTheaterMovieList().remove(movie);
        movie.getScreenings().removeIf(screeningTime -> screeningTime.getBranch().getLocation().equals(branch.getLocation()));
    }
    public void AddToMovieList(Branch branch, InTheaterMovie movie){
        branch.getInTheaterMovieList().add(movie);
    }
    public void UpdateMovieScreeningTime(InTheaterMovie movie, ScreeningTime time, LocalTime newTime){
        ScreeningTime updatedTime = new ScreeningTime(time.getBranch(), time.getDay(), newTime, time.getTheater() );
        movie.getScreenings().removeIf(screeningTime -> screeningTime.getId() == time.getId());
        movie.getScreenings().add(updatedTime);
    }
}
