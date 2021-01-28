package diiage.potherat.demo.demoapp3.ui.home;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import diiage.potherat.demo.demoapp3.dal.repository.QuoteRepository;
import diiage.potherat.demo.demoapp3.model.Quote;

public class HomeViewModel extends ViewModel {
    private QuoteRepository quoteRepository;

    public MutableLiveData<Integer> numberOfQuotes;
    public MutableLiveData<Integer> numberOfSources;
    public MutableLiveData<Quote> lastQuote;

    @Inject
    @ViewModelInject
    public HomeViewModel(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
        numberOfQuotes = new MutableLiveData<>();
        numberOfSources = new MutableLiveData<>();
        lastQuote = new MutableLiveData<>();
    }



    public void loadNumberOfQuotes() {
        new Thread(new Runnable() {
            public void run() {
                Integer result = quoteRepository.getCountQuote();
                numberOfSources.postValue(result);
            }
        }).start();
    }

    public void loadNumberDistinctOfSources() {
        new Thread(new Runnable() {
            public void run() {
                Integer result = quoteRepository.getCountSource();
                numberOfQuotes.postValue(result);
            }
        }).start();
    }

    public void loadLastQuote() {
        new Thread(new Runnable() {
            public void run() {
                Quote result = quoteRepository.getLastAuthorQuote();
                lastQuote.postValue(result);
            }
        }).start();
    }
}