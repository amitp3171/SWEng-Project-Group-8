package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import java.time.LocalTime;

public class ContentManager extends AbstractEmployee {

    public ContentManager(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password);
    }

    public void removeFromMovieList(Branch branch, InTheaterMovie movie){
        branch.getInTheaterMovieList().remove(movie);
        movie.getScreenings().removeIf(screeningTime -> screeningTime.getBranch().getLocation().equals(branch.getLocation()));
    }

    public void addToMovieList(Branch branch, InTheaterMovie movie){
        branch.getInTheaterMovieList().add(movie);
    }

    public void updateMovieScreeningTime(ScreeningTime time, LocalTime newTime){
        time.setTime(newTime);
    }

    public void addMovieScreeningTime(InTheaterMovie movie, ScreeningTime time){
        movie.addScreeningTime(time);
    }

    public void removeMovieScreeningTime(InTheaterMovie movie, ScreeningTime time){
        movie.removeScreeningTime(time);
    }
}