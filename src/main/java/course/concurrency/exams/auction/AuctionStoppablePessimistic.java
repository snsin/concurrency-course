package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(null, null, Long.MIN_VALUE);
    private volatile boolean isStopped = false;

    private final Object monitor = new Object();

    public boolean propose(Bid bid) {
        if (isStopped) {
            return false;
        }
        synchronized (monitor) {
            if (bid.getPrice() > latestBid.getPrice()) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
            return false;
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        synchronized (monitor) {
            this.isStopped = true;
        }
        return latestBid;
    }
}
