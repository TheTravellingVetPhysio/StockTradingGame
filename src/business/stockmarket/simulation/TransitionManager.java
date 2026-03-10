package business.stockmarket.simulation;

import shared.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TransitionManager {
  private static final TransitionManager instance = new TransitionManager();
  private static final Random random = new Random();

  private final Map<LiveStock, Integer> consecutiveDeclines = new HashMap<>();
  private final Map<LiveStock, Integer> consecutiveGrowth = new HashMap<>();
  private final Map<LiveStock, Integer> bankruptTicks = new HashMap<>();
  private final Map<LiveStock, Integer> bankruptDuration = new HashMap<>();

  private TransitionManager() {}

  public static TransitionManager getInstance() {
    return instance;
  }

  public void transition(LiveStock stock, double changePercent) {
    String currentState = stock.getCurrentStateName();

    switch (currentState) {
      case "SteadyState" -> handleSteady(stock);
      case "GrowingState" -> handleGrowing(stock, changePercent);
      case "DecliningState" -> handleDeclining(stock, changePercent);
      case "BankruptState" -> handleBankrupt(stock);
      case "ResetState" -> handleReset(stock);
    }
  }

  private void handleSteady(LiveStock stock) {
    double rand = random.nextDouble();
    if (rand < 0.08) {
      transitionTo(stock, new GrowingState());
    } else if (rand < 0.14) {
      transitionTo(stock, new DecliningState());
    }
  }

  private void handleGrowing(LiveStock stock, double changePercent) {
    int declines = consecutiveDeclines.getOrDefault(stock, 0);

    if (changePercent < 0) {
      declines++;
    } else {
      declines = 0;
    }
    consecutiveDeclines.put(stock, declines);

    double rand = random.nextDouble();

    if (declines >= 3) {
      if (rand < 0.6) {
        transitionTo(stock, new SteadyState());
      } else if (rand < 0.85) {
        transitionTo(stock, new DecliningState());
      }
    } else if (declines >= 2) {
      if (rand < 0.35) {
        transitionTo(stock, new SteadyState());
      } else if (rand < 0.5) {
        transitionTo(stock, new DecliningState());
      }
    } else if (declines >= 1) {
      if (rand < 0.07) {
        transitionTo(stock, new SteadyState());
      } else if (rand < 0.1) {
        transitionTo(stock, new DecliningState());
      }
    }
  }

  private void handleDeclining(LiveStock stock, double changePercent) {
    int growth = consecutiveGrowth.getOrDefault(stock, 0);

    if (changePercent > 0) {
      growth++;
    } else {
      growth = 0;
    }
    consecutiveGrowth.put(stock, growth);

    double rand = random.nextDouble();

    if (growth >= 3) {
      if (rand < 0.6) {
        transitionTo(stock, new SteadyState());
      } else if (rand < 0.85) {
        transitionTo(stock, new GrowingState());
      }
    } else if (growth >= 2) {
      if (rand < 0.35) {
        transitionTo(stock, new SteadyState());
      } else if (rand < 0.5) {
        transitionTo(stock, new GrowingState());
      }
    } else if (growth >= 1) {
      if (rand < 0.07) {
        transitionTo(stock, new SteadyState());
      } else if (rand < 0.1) {
        transitionTo(stock, new GrowingState());
      }
    }
  }

  private void handleBankrupt(LiveStock stock) {
    int ticks = bankruptTicks.getOrDefault(stock, 0) + 1;
    int duration = bankruptDuration.getOrDefault(stock, random.nextInt(21) + 5);

    bankruptTicks.put(stock, ticks);
    bankruptDuration.put(stock, duration);

    if (ticks >= duration) {
      bankruptTicks.remove(stock);
      bankruptDuration.remove(stock);
      transitionTo(stock, new ResetState());
    }
  }

  private void handleReset(LiveStock stock) {
    transitionTo(stock, new SteadyState());
  }

  private void transitionTo(LiveStock stock, StockState newState) {
    String formerState = stock.getCurrentStateName();
    stock.setState(newState);
    logTransition(stock, formerState);
  }

  private void logTransition(LiveStock stock, String formerState) {
    Logger.getInstance().log("INFO",
        stock.getSymbol() + " transitioning from " + formerState
            + " to " + stock.getCurrentStateName());
  }
}